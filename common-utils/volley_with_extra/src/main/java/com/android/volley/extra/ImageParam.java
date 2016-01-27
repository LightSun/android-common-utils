package com.android.volley.extra;

public class ImageParam {

	public float roundSize;
	public Type type;
	public int defaultImageResId;
	public int errorImageResId;
	
	/*public*/ ImageParam(){}
	public ImageParam(float roundSize, Type type) {
		super();
		this.roundSize = roundSize;
		this.type = type;
	}
	
	public void setDefaultImageResId(int resId){
		this.defaultImageResId = resId;
	}
	
	public void setErrorImageResId(int resId){
		this.errorImageResId = resId;
	}

	public static enum Type{
		
		Round(1) , Circle(2);
		final int value;
		
		private Type(int value) {
			this.value = value;
		}
	}
	public static class Builder{
		private final ImageParam mParam = new ImageParam();
		
		/** indicate the four corner of image will be round*/
		public Builder round(float roundSize){
			mParam.roundSize = roundSize;
			if(mParam.type != Type.Round){
				mParam.type = Type.Round;
			}
			return this;
		}
		/** set the image to circle.the radio is the width or height which is lower.*/
		public Builder circle(){
			if(mParam.type != Type.Circle){
				mParam.type = Type.Circle;
			}
			return this;
		}
		/** @see {@link ExpandNetworkImageView#setDefaultImageResId(int)} */
		public Builder placeholder(int defaultImageResId){
			mParam.defaultImageResId = defaultImageResId;
			return this; 
		}
		/** @see {@link ExpandNetworkImageView#setErrorImageResId(int)} */
		public Builder error(int errorImageResId){
			mParam.errorImageResId = errorImageResId;
			return this; 
		}
		
		public ImageParam create(){
			return mParam;
		}
	}
}
