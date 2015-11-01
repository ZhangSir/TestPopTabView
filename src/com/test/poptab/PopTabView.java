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
 * ��ѡ��ѡ�Ŵ��TabView
 * @author zhangshuo
 */
public class PopTabView extends LinearLayout{

	private Context mContext;
	/**���Ĭ�Ͽ��*/
	private int DEFAULT_WIDTH = 720;
	/**���Ĭ�ϸ߶�*/
	private int DEFAULT_HEIGHT = 1080;
	/**pop��ѡ��ʱ��Ĭ�Ͽ�ȣ���Ⱥ͸߶���ͬ��*/
	private int DEFAULT_POP_SELECTED_WIDTH = 96;
	/**popδ��ѡ��ʱ��Ĭ�Ͽ�ȣ���Ⱥ͸߶���ͬ��*/
	private int DEFAULT_POP_UNSELECTED_WIDTH = 64;
	/**pop���Ĭ�Ͼ���*/
	private int DEFAULT_SPACE = 16;
	
	
	/**pop��ľ���*/
	private int space = DEFAULT_SPACE;
	/**pop��ѡ��ʱ�Ŀ�ȣ���Ⱥ͸߶���ͬ��*/
	private int popSelectedWidth = DEFAULT_POP_SELECTED_WIDTH;
	/**popδ��ѡ��ʱ�Ŀ�ȣ���Ⱥ͸߶���ͬ��*/
	private int popUnselectedWidth = DEFAULT_POP_UNSELECTED_WIDTH;
	/**��ǰѡ�е�popλ��*/
	private int selectedIndex = 0;
	/**�ڳ�ʼ������ʱ����ʾ�Ƿ�������磬����HorizontalScrollView�п��Ժ������
	 * �����ô˲���Ϊtrueʱ��pop�Ŀ�߻��������ֶ����õ�Ϊ׼��
	 * falseʱ�����ݵ�ǰ�������ȥ�������width������õ�pop�Ŀ��*/
	private boolean overstepable = false;
	
	/**onLayout�Ǳ�ʾ�Ƿ��Ѿ���ʼ����*/
	private boolean inited = false;
	/** ��־��ǰ�Ƿ�����ִ�ж���*/
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
	 * ��ʼ��������
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
				Toast.makeText(mContext, "ѡ��" + selectedIndex, Toast.LENGTH_SHORT).show();
			}
		});
		va.start();
		
	}
	
	/**
	 * ��ȡPop�л�ʱ�Ļص��ӿ�
	 * @return
	 */
	public OnPopSwitchListener getOnPopSwitchListener() {
		return onPopSwitchListener;
	}

	/**
	 * ����Pop�л�ʱ�Ļص��ӿ�
	 * @param onPopSwitchListener
	 */
	public void setOnPopSwitchListener(OnPopSwitchListener onPopSwitchListener) {
		this.onPopSwitchListener = onPopSwitchListener;
	}

	/**
	 * ��ȡ����������
	 * @return
	 */
	public BaseAdapter getmAdapter() {
		return mAdapter;
	}

	/**
	 * ��������������
	 * @param mAdapter
	 */
	public void setmAdapter(BaseAdapter mAdapter) {
		this.mAdapter = mAdapter;
	}
	
	/**
	 * ��ȡ��ǰѡ�е�pop��position
	 * @return
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}

	/**
	 * ����ѡ�е�pop
	 * @param selectedIndex
	 */
	public void setSelectedIndex(int selectedIndex) {
		if(this.selectedIndex != selectedIndex){
			scale(this.selectedIndex, selectedIndex);
		}
	}
	
	/**
	 * �������б仯�ǣ����ø÷�������ˢ�½���
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
							//��ǰδѡ��
							scale(selectedIndex, ((Integer)v.getTag()).intValue());
						}else if(selectedIndex == ((Integer)v.getTag()).intValue()){
							//��ǰ��ѡ��
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
	 * Pop�л�ʱ�Ļص��ӿ�
	 * @author zhangshuo
	 */
	public interface OnPopSwitchListener{
		/**
		 * ѡ���µ�popʱ�Ļص�����
		 * @param position ��ǰ��ѡ�е�pop��position
		 */
		public void onPopSwitch(int position);
		/**
		 * ��ѡ�е�pop�ٴα����ʱ�Ļص�����
		 * @param position
		 */
		public void onPopSelectedAgain(int position);
		
		/**
		 * ��������
		 * @param narrowView ������С��View
		 * @param enlargeView ���ڷŴ��View
		 * @param franch ����ִ�аٷֱ�
		 */
		public void onAnimationUpdate(View narrowView, View enlargeView, float franch);
	}

}
