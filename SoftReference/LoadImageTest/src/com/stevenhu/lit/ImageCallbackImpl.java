package com.stevenhu.lit;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.stevenhu.lit.AsyncImageLoader.ImageCallback;

public class ImageCallbackImpl implements ImageCallback
{

	private ImageView mImageView;
	
	public ImageCallbackImpl(ImageView imageView)
	{
		mImageView = imageView;
	}
	
	//��ImageView����ʾ�����ϻ�ȡ��ͼƬ
	@Override
	public void imageLoad(Drawable drawable) 
	{
		// TODO Auto-generated method stub
		mImageView.setImageDrawable(drawable);
	}

}
