package com.test.poptab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * 被选中选放大的TabView
 * @author zhangshuo
 */
public class PopTabView extends LinearLayout{

	private Context mContext;
	/**组件默认宽度*/
	private int DEFAULT_WIDTH = 720;
	/**组件默认高度*/
	private int DEFAULT_HEIGHT = 1080;
	/**pop被选中时的默认宽度（宽度和高度相同）*/
	private int DEFAULT_POP_SELECTED_WIDTH = 96;
	/**pop未被选中时的默认宽度（宽度和高度相同）*/
	private int DEFAULT_POP_UNSELECTED_WIDTH = 64;
	/**pop间的默认距离*/
	private int DEFAULT_SPACE = 16;
	
	
	/**pop间的距离*/
	private int space = DEFAULT_SPACE;
	/**pop被选中时的宽度（宽度和高度相同）*/
	private int popSelectedWidth = DEFAULT_POP_SELECTED_WIDTH;
	/**pop未被选中时的宽度（宽度和高度相同）*/
	private int popUnselectedWidth = DEFAULT_POP_UNSELECTED_WIDTH;
	/**当前选中的pop位置*/
	private int selectedIndex = 0;
	/**在初始化布局时，标示是否允许国界，即在HorizontalScrollView中可以横向滚动
	 * 当设置此参数为true时，pop的宽高会优先以手动设置的为准；
	 * false时，根据当前组件可以去到的最大width，计算得到pop的宽高*/
	private boolean overstepable = false;
	
	/**onLayout是标示是否已经初始化过*/
	private boolean inited = false;
	/** 标志当前是否正在执行动画*/
	private boolean isMoving = false;
	
	private BaseAdapter mAdapter = null;
	
	private OnPopSwitchListener onPopSwitchListener = null;
	
	public PopTabView(Context context) {
		this(context, null);
	}

	public PopTabView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PopTabView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.init(context, attrs, defStyle);
	}

	private void init(Context context, AttributeSet attrs, int defStyle){
		
		this.mContext = context;
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setGravity(Gravity.CENTER);
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.pop_tabview);
		initStyle(a);
		a.recycle();
		
	}
	
	/**
	 * 初始化主题风格
	 * @param typedArray
	 */
	private void initStyle(TypedArray typedArray){
		if(typedArray.getIndexCount() > 0){
			int typedCount = typedArray.getIndexCount();
			int typed = -1;
			for (int i=0; i<typedCount; i++) {
				typed = typedArray.getIndex(i);
				if (R.styleable.pop_tabview_popTabViewStyle == typed) {
					int resId = typedArray.getResourceId(typed, 0);
					TypedArray a1 = mContext.obtainStyledAttributes(resId, R.styleable.pop_tabview);
					initStyle(a1);
					a1.recycle();
				}else if(R.styleable.pop_tabview_overstepable == typed){
					overstepable = typedArray.getBoolean(typed, false);
				}else if(R.styleable.pop_tabview_popSelectedWidth == typed){
					popSelectedWidth = typedArray.getDimensionPixelSize(typed, DEFAULT_POP_SELECTED_WIDTH);
				}else if(R.styleable.pop_tabview_popUnselectedWidth == typed){
					popUnselectedWidth = typedArray.getDimensionPixelSize(typed, DEFAULT_POP_UNSELECTED_WIDTH);
				}else if(R.styleable.pop_tabview_space == typed){
					space = typedArray.getDimensionPixelSize(typed, DEFAULT_SPACE);
				}
			}
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = measureDimension(DEFAULT_WIDTH, widthMeasureSpec);
		int height = measureDimension(DEFAULT_HEIGHT, heightMeasureSpec);
		if(overstepable && null != mAdapter){
			width = (mAdapter.getCount()-1) * popUnselectedWidth + popSelectedWidth;
		}
		setMeasuredDimension(width, height);
	}
	
	public int measureDimension(int defaultSize, int measureSpec) {
		int result;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = defaultSize; // UNSPECIFIED
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		
		if(!inited){
			inited = true;
			if(!overstepable){
				popUnselectedWidth = (int) ((getWidth() - getPaddingLeft() - getPaddingRight() - ((mAdapter.getCount() - 1) * space))/(mAdapter.getCount() + 0.5));
				popSelectedWidth = (int) (1.5 * popUnselectedWidth);
			}
			
			this.getLayoutParams().height = popSelectedWidth + getPaddingBottom() + getPaddingTop();
			
			for(int i = 0; i < this.getChildCount(); i++){
				if(i == selectedIndex){
					this.getChildAt(i).getLayoutParams().height = popSelectedWidth;
					this.getChildAt(i).getLayoutParams().width = popSelectedWidth;
				}else{
					this.getChildAt(i).getLayoutParams().height = popUnselectedWidth;
					this.getChildAt(i).getLayoutParams().width = popUnselectedWidth;
				}
				
				if(i != 0){
					LayoutParams lp = (LayoutParams) this.getChildAt(i).getLayoutParams();
					lp.leftMargin = space;
					this.getChildAt(i).setLayoutParams(lp);
				}
			}
		}
	}
	
	private void scale(final int narrowIndex, final int enlargeIndex){
		ValueAnimator va = ValueAnimator.ofInt(popSelectedWidth, popUnselectedWidth);
		va.setInterpolator(new BounceInterpolator());
		va.setDuration(1500);
		final LayoutParams narrowLP = (LayoutParams) getChildAt(narrowIndex).getLayoutParams();
		final LayoutParams enlargeLP = (LayoutParams) getChildAt(enlargeIndex).getLayoutParams();
		va.addUpdateListener(new AnimatorUpdateListener() {
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				narrowLP.height = (int) animation.getAnimatedValue();
				narrowLP.width = (int) animation.getAnimatedValue();
				getChildAt(narrowIndex).setLayoutParams(narrowLP);
				
				enlargeLP.height = enlargeLP.width = popUnselectedWidth + popSelectedWidth - (int)animation.getAnimatedValue();
				getChildAt(enlargeIndex).setLayoutParams(enlargeLP);
				
				if(null != onPopSwitchListener){
					onPopSwitchListener.onAnimationUpdate(getChildAt(narrowIndex), getChildAt(enlargeIndex), animation.getAnimatedFraction());
				}
			}
		});
		va.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				isMoving = true;
			}
			@Override
			public void onAnimationEnd(Animator animation) {
				isMoving = false;
				selectedIndex = enlargeIndex;
				if(null != onPopSwitchListener){
					onPopSwitchListener.onPopSwitch(selectedIndex);
				}
				Toast.makeText(mContext, "选中" + selectedIndex, Toast.LENGTH_SHORT).show();
			}
		});
		va.start();
		
	}
	
	/**
	 * 获取Pop切换时的回调接口
	 * @return
	 */
	public OnPopSwitchListener getOnPopSwitchListener() {
		return onPopSwitchListener;
	}

	/**
	 * 设置Pop切换时的回调接口
	 * @param onPopSwitchListener
	 */
	public void setOnPopSwitchListener(OnPopSwitchListener onPopSwitchListener) {
		this.onPopSwitchListener = onPopSwitchListener;
	}

	/**
	 * 获取内容适配器
	 * @return
	 */
	public BaseAdapter getmAdapter() {
		return mAdapter;
	}

	/**
	 * 设置内容适配器
	 * @param mAdapter
	 */
	public void setmAdapter(BaseAdapter mAdapter) {
		this.mAdapter = mAdapter;
	}
	
	/**
	 * 获取当前选中的pop的position
	 * @return
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}

	/**
	 * 设置选中的pop
	 * @param selectedIndex
	 */
	public void setSelectedIndex(int selectedIndex) {
		if(this.selectedIndex != selectedIndex){
			scale(this.selectedIndex, selectedIndex);
		}
	}
	
	/**
	 * 当数据有变化是，调用该方法进行刷新界面
	 */
	public void notifyDataSetChanged(){
		this.removeAllViews();
		if(null == mAdapter) return;
		for(int i = 0; i < mAdapter.getCount(); i++){
			View view = mAdapter.getView(i, null, null);
			view.setTag(i);
			this.addView(view);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(!isMoving){
						if(selectedIndex != ((Integer)v.getTag()).intValue()){
							//当前未选中
							scale(selectedIndex, ((Integer)v.getTag()).intValue());
						}else if(selectedIndex == ((Integer)v.getTag()).intValue()){
							//当前已选中
							if(null != onPopSwitchListener){
								onPopSwitchListener.onPopSelectedAgain(((Integer)v.getTag()).intValue());
							}
						}
						
					}
				}
			});
		}
		
		inited = false;
		postInvalidate();
		
	}

	/**
	 * Pop切换时的回调接口
	 * @author zhangshuo
	 */
	public interface OnPopSwitchListener{
		/**
		 * 选中新的pop时的回调函数
		 * @param position 当前被选中的pop的position
		 */
		public void onPopSwitch(int position);
		/**
		 * 被选中的pop再次被点击时的回调函数
		 * @param position
		 */
		public void onPopSelectedAgain(int position);
		
		/**
		 * 动画更新
		 * @param narrowView 正在缩小的View
		 * @param enlargeView 正在放大的View
		 * @param franch 动画执行百分比
		 */
		public void onAnimationUpdate(View narrowView, View enlargeView, float franch);
	}

}
