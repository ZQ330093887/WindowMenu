package com.code_tinker.window.menu.utils;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.code_tinker.window.menu.R;
import com.code_tinker.window.menu.adapter.GridViewAdapter;
import com.code_tinker.window.menu.adapter.ViewPagerAdapter;
import com.code_tinker.window.menu.bean.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouqiong on 2017/6/9.
 */
public class PopupMenuUtil {

    private static final String TAG = "PopupMenuUtil";

    public static PopupMenuUtil getInstance() {
        return MenuUtilHolder.INSTANCE;
    }

    private String[] titles = {"美食", "电影", "酒店住宿", "休闲娱乐", "外卖", "自助餐", "KTV", "机票/火车票", "周边游", "美甲美睫",
            "火锅", "生日蛋糕", "甜品饮品", "水上乐园", "汽车服务", "美发", "丽人", "景点", "足疗按摩", "运动健身", "健身", "超市", "买菜",
            "今日新单", "小吃快餐", "面膜", "洗浴/汗蒸", "母婴亲子", "生活服务", "婚纱摄影", "学习培训", "家装", "结婚", "全部分配"};

    private static class MenuUtilHolder {
        public static PopupMenuUtil INSTANCE = new PopupMenuUtil();
    }

    private View rootVew;
    private PopupWindow popupWindow;
    private ViewPager mPager;
    private LinearLayout mLlDot;
    private RelativeLayout rlClick;
    private ImageView ivBtn;
    private List<Model> mDatas;
    private LayoutInflater inflater;
    private List<View> mPagerList;

    /**
     * 动画执行的 属性值数组
     */
    float animatorProperty[] = null;
    /**
     * 第一排图 距离屏幕底部的距离
     */
    int top = 0;
    /**
     * 第二排图 距离屏幕底部的距离
     */
    int bottom = 0;

    /**
     * 总的页数
     */
    private int pageCount;
    /**
     * 每一页显示的个数
     */
    private int pageSize = 10;
    /**
     * 当前显示的是第几页
     */
    private int curIndex = 0;

    /**
     * 创建 popupWindow 内容
     *
     * @param context context
     */
    private void _createView(final Context context) {
        rootVew = LayoutInflater.from(context).inflate(R.layout.popup_menu, null);
        popupWindow = new PopupWindow(rootVew,
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        //设置为失去焦点 方便监听返回键的监听
        popupWindow.setFocusable(false);

        // 如果想要popupWindow 遮挡住状态栏可以加上这句代码
        //popupWindow.setClippingEnabled(false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(false);

        if (animatorProperty == null) {
            top = dip2px(context, 310);
            bottom = dip2px(context, 210);
            animatorProperty = new float[]{bottom, 60, -30, -20 - 10, 0};
        }

        initLayout(context);
    }

    /**
     * dp转化为px
     *
     * @param context  context
     * @param dipValue dp value
     * @return 转换之后的px值
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 初始化 view
     */
    private void initLayout(final Context context) {
        //初始化数据源
        initDatas(context);
        rlClick = (RelativeLayout) rootVew.findViewById(R.id.pop_rl_click);
        ivBtn = (ImageView) rootVew.findViewById(R.id.pop_iv_img);
        mPager = (ViewPager) rootVew.findViewById(R.id.viewpager);
        mLlDot = (LinearLayout) rootVew.findViewById(R.id.ll_dot);


        rlClick.setOnClickListener(new MViewClick(0, context));
        mPager.setOnClickListener(new MViewClick(1, context));
        mLlDot.setOnClickListener(new MViewClick(2, context));

        inflater = LayoutInflater.from(context);
        //总的页数=总数/每页数量，并取整
        pageCount = (int) Math.ceil(mDatas.size() * 1.0 / pageSize);
        mPagerList = new ArrayList<>();
        for (int i = 0; i < pageCount; i++) {
            // 每个页面都是inflate出一个新实例
            GridView gridView = (GridView) inflater.inflate(R.layout.gridview, mPager, false);
            gridView.setAdapter(new GridViewAdapter(context, mDatas, i, pageSize));
            mPagerList.add(gridView);

            gridView.setOnItemClickListener((parent, view, position, id) -> {
                int pos = position + curIndex * pageSize;
                Toast.makeText(context, mDatas.get(pos).getName(), Toast.LENGTH_SHORT).show();
            });
        }
        //设置适配器
        mPager.setAdapter(new ViewPagerAdapter(mPagerList));
        //设置圆点
        setOvalLayout();
    }

    /**
     * 点击事件
     */
    private class MViewClick implements View.OnClickListener {

        public int index;
        public Context context;

        public MViewClick(int index, Context context) {
            this.index = index;
            this.context = context;
        }

        @Override
        public void onClick(View v) {
            if (index == 0) {
                //加号按钮点击之后的执行
                _rlClickAction();
            } else {
                showToast(context, "index=" + index);
            }
        }
    }

    Toast toast = null;

    /**
     * 防止toast 多次被创建
     *
     * @param context context
     * @param str     str
     */
    private void showToast(Context context, String str) {
        if (toast == null) {
            toast = Toast.makeText(context, str, Toast.LENGTH_SHORT);
        } else {
            toast.setText(str);
        }
        toast.show();
    }

    /**
     * 刚打开popupWindow 执行的动画
     */
    private void _openPopupWindowAction() {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ivBtn, "rotation", 0f, 135f);
        objectAnimator.setDuration(200);
        objectAnimator.start();

        _startAnimation(mPager, 500, animatorProperty);
        /*_startAnimation(mPager, 430, animatorProperty);
        _startAnimation(mPager, 430, animatorProperty);
        _startAnimation(mPager, 500, animatorProperty);

        _startAnimation(mPager, 500, animatorProperty);
        _startAnimation(mPager, 430, animatorProperty);
        _startAnimation(mPager, 430, animatorProperty);
        _startAnimation(mPager, 500, animatorProperty);*/
    }

    /**
     * 初始化数据源
     */
    private void initDatas(Context mContext) {
        mDatas = new ArrayList<>();
        for (int i = 0; i < titles.length; i++) {
            //动态获取资源ID，第一个参数是资源名，第二个参数是资源类型例如drawable，string等，第三个参数包名
            int imageId = mContext.getResources().getIdentifier("ic_category_" + i, "mipmap", mContext.getPackageName());
            mDatas.add(new Model(titles[i], imageId));
        }
    }


    /**
     * 关闭 popupWindow执行的动画
     */
    public void _rlClickAction() {
        if (ivBtn != null && rlClick != null) {

            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(ivBtn, "rotation", 135f, 0f);
            objectAnimator.setDuration(300);
            objectAnimator.start();
            _closeAnimation(mPager, 300, top);

        /*    _closeAnimation(mPager, 300, top);
            _closeAnimation(mPager, 200, top);
            _closeAnimation(mPager, 200, top);
            _closeAnimation(mPager, 300, top);
            _closeAnimation(mPager, 300, bottom);
            _closeAnimation(mPager, 200, bottom);
            _closeAnimation(mPager, 200, bottom);
            _closeAnimation(mPager, 300, bottom);*/

            rlClick.postDelayed(() -> _close(), 300);

        }
    }


    /**
     * 弹起 popupWindow
     *
     * @param context context
     * @param parent  parent
     */
    public void _show(Context context, View parent) {
        _createView(context);
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(parent, Gravity.NO_GRAVITY, 0, 0);
            _openPopupWindowAction();
        }
    }

    /**
     * 关闭popupWindow
     */

    public void _close() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    /**
     * @return popupWindow 是否显示了
     */
    public boolean _isShowing() {
        if (popupWindow == null) {
            return false;
        } else {
            return popupWindow.isShowing();
        }
    }

    /**
     * 关闭 popupWindow 时的动画
     *
     * @param view     mView
     * @param duration 动画执行时长
     * @param next     平移量
     */
    private void _closeAnimation(View view, int duration, int next) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationY", 0f, next);
        anim.setDuration(duration);
        anim.start();
    }

    /**
     * 启动动画
     *
     * @param view     view
     * @param duration 执行时长
     * @param distance 执行的轨迹数组
     */
    private void _startAnimation(View view, int duration, float[] distance) {
        ObjectAnimator anim = ObjectAnimator.ofFloat(view, "translationY", distance);
        anim.setDuration(duration);
        anim.start();
    }

    /**
     * 设置圆点
     */
    public void setOvalLayout() {
        for (int i = 0; i < pageCount; i++) {
            mLlDot.addView(inflater.inflate(R.layout.dot, null));
        }
        // 默认显示第一页
        mLlDot.getChildAt(0).findViewById(R.id.v_dot)
                .setBackgroundResource(R.drawable.dot_selected);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int position) {
                // 取消圆点选中
                mLlDot.getChildAt(curIndex)
                        .findViewById(R.id.v_dot)
                        .setBackgroundResource(R.drawable.dot_normal);
                // 圆点选中
                mLlDot.getChildAt(position)
                        .findViewById(R.id.v_dot)
                        .setBackgroundResource(R.drawable.dot_selected);
                curIndex = position;
            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }
}
