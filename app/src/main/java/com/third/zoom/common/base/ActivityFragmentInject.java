package com.third.zoom.common.base;


import com.third.zoom.R;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ActivityFragmentInject {

    /**
     * 顶部局的id
     *
     * @return
     */
    int contentViewId() default R.layout.activity_main;

    /**
     * 是否存在NavigationView
     *
     * @return
     */
    boolean hasNavigationView() default false;

    /**
     * 是否存在Toolbar
     *
     * @return
     */
    boolean hasToolbar() default false;

    /**
     * toolbar的标题id
     *
     * @return
     */
    int toolbarTitle() default -1;

    /**
     * 左边文本资源id
     * @return
     */
    int toolbarLeftText() default -1;

    /**
     * 右边文本资源id
     * @return
     */
    int toolbarRightText() default -1;

    /**
     * 左边图片资源id
     * @return
     */
    int toolbarLeftIcon() default -1;

    /**
     * 右边图片资源id
     * @return
     */
    int toolbarRightIcon() default -1;

}
