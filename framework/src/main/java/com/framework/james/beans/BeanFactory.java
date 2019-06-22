package com.framework.james.beans;

import com.framework.james.web.mvc.Controller;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory {
    /**
     * 将类转换成Bean之后，统一存放的容器
     * 注：Controller也是特殊的Bean
     */
    private static Map<Class<?>, Object> classToBean = new ConcurrentHashMap<>();

    public static Object getBean(Class<?> cls) {
        return classToBean.get(cls);
    }

    /**
     * Spring启动后会将指定的类初始化成Bean工厂
     *
     * @param classList Spring启动时通过包扫描获得的类容器
     * @throws Exception
     */
    public static void initBean(List<Class<?>> classList) throws Exception {
        // 待生成为Bean的List
        List<Class<?>> toCreateList = new ArrayList<>(classList);
        // 无需生成为Bean的List
        List<Class<?>> finishedList = new ArrayList<>();
        while (toCreateList.size() != 0) {
            for (Class cls : toCreateList) {
                // 类处理完成后,就加入finishedList
                if (finishCreate(cls)) {
                    finishedList.add(cls);
                }
            }
            // 若存在创建完成的List, 下轮将不再处理
            if (finishedList.size() > 0 ) {
                toCreateList.removeAll(finishedList);
            }
            // todo 避免循环依赖而造成的死循环
        }
    }

    /**
     * 处理传入的类
     * 需要生成Bean的：
     *   1.1 被Bean或者Controller注解 --> 生成Bean
     *   1.2 被1.中注解的类中含有Autowired注解的 --> 绑定Bean之间的关系
     * 不需要生成Bean的：
     *   2.1 不符合 1 中的情况
     *   2.2 已经生成过Bean
     * @param cls
     * @return 处理过程是否出异常
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private static boolean finishCreate(Class<?> cls) throws Exception {
        if (!cls.isAnnotationPresent(Bean.class) && !cls.isAnnotationPresent(Controller.class)) {
            return true;
        }
        Object bean = cls.newInstance();
        // 反射获取所有属性，处理Bean依赖Bean的情况
        for (Field field : cls.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                // 获取该类含有@Autowired注解的属性的类(依赖)
                Class<?> fieldType = field.getType();
                // 将依赖的类转换为Bean
                Object reliantBean = BeanFactory.getBean(fieldType);
                if (reliantBean == null) {
                    // todo 若工厂还未生成Bean依赖，标记为未处理，这里直接返回false等待下次处理。
                    // todo 处理完无依赖的@Bean和@Controller注解，@Autowired注解自然就能处理
                    return false;
                } else {
                    // 若存在@Autowired注解的属性Bean已创建，打开该属性的访问权限
                    field.setAccessible(true);
                    // 设置上层bean的field指向reliantBean
                    field.set(bean, reliantBean);
                }
            }
        }
        // 处理完依赖后，加入Bean工厂的容器内
        classToBean.put(cls,bean);
        return true;
    }
}
