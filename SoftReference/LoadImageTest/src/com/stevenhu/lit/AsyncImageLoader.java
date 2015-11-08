package com.stevenhu.lit;

import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

//ʵ��ͼƬ�첽���ص���
public class AsyncImageLoader 
{
	//��UrlΪ����SoftReferenceΪֵ����������HashMap��ֵ�ԡ�
	private Map<String, SoftReference<Drawable>> mImageCache = 
		new HashMap<String, SoftReference<Drawable>>();
	
	//ʵ��ͼƬ�첽����
	public Drawable loadDrawable(final String imageUrl, final ImageCallback callback)
	{
		//��ѯ���棬�鿴��ǰ��Ҫ���ص�ͼƬ�Ƿ��ڻ�����
		if(mImageCache.containsKey(imageUrl))
		{
			SoftReference<Drawable> softReference = mImageCache.get(imageUrl);
			if (softReference.get() != null)
			{
				return softReference.get();
			}
		}
		
		final Handler handler = new Handler()
		{
			@Override
			public void dispatchMessage(Message msg) 
			{
				//�ص�ImageCallbackImpl�е�imageLoad������������(UI�߳�)��ִ�С�
				callback.imageLoad((Drawable)msg.obj);
			}
		};
		
		/*��������û�У��¿���һ���̣߳����ڽ��д�����������ͼƬ��
		 * Ȼ�󽫻�ȡ����Drawable���͵�Handler�д���ͨ���ص�ʵ����UI�߳�����ʾ��ȡ��ͼƬ
		 */
		new Thread()
		{		
			public void run() 
			{
				Drawable drawable = loadImageFromUrl(imageUrl);
				//���õ���ͼƬ��ŵ�������
				mImageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			};
		}.start();
		
		//�������в����ڣ���������������ʾ��ɺ󣬴˴�����null��
		return null;
		
	}
	
	//����һ���ص��ӿ�
	public interface ImageCallback
	{
		void imageLoad(Drawable drawable);
	}
	
	//ͨ��Url�����ϻ�ȡͼƬDrawable����
	protected Drawable loadImageFromUrl(String imageUrl)
	{
		try {
			return Drawable.createFromStream(new URL(imageUrl).openStream(),"debug");
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}
}

