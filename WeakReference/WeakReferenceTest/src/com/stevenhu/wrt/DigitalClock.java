package com.stevenhu.wrt;

import java.lang.ref.WeakReference;
import java.text.DateFormatSymbols;
import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Canvas;
import android.os.Handler;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DigitalClock extends LinearLayout {
	// 12Сʱ��24Сʱ��
	private final static String M12 = "h:mm";
	private final static String M24 = "kk:mm";

	private Calendar mCalendar;
	private String mFormat;
	private TextView mDislpayTime;
	private AmPm mAmPm;
	private ContentObserver mFormatChangeObserver;
	private final Handler mHandler = new Handler();
	private BroadcastReceiver mReceiver;
	private Context mContext;

	public DigitalClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		mDislpayTime = (TextView) this.findViewById(R.id.time);
		mAmPm = new AmPm(this);
		mCalendar = Calendar.getInstance();
		//����ʱ����ʾ��ʽ
		setDateFormat();
	}

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		
		//��̬ע�����ʱ��ı�Ĺ㲥
		if (mReceiver == null) {
			mReceiver = new TimeChangedReceiver(this);
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_TIME_CHANGED);
			filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
			mContext.registerReceiver(mReceiver, filter);
		}

		//ע�����ʱ���ʽ�ı��ContentObserver
		if (mFormatChangeObserver == null) {
			mFormatChangeObserver = new FormatChangeObserver(this);
			mContext.getContentResolver().registerContentObserver(
					Settings.System.CONTENT_URI, true, mFormatChangeObserver);
		}

		//����ʱ��
		updateTime();
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();

		if (mReceiver != null) {
			mContext.unregisterReceiver(mReceiver);
		}
		if (mFormatChangeObserver != null) {
			mContext.getContentResolver().unregisterContentObserver(
					mFormatChangeObserver);
		}

		mFormatChangeObserver = null;
		mReceiver = null;
	}

	static class AmPm {
		private TextView mAmPmTextView;
		private String mAmString, mPmString;

		AmPm(View parent) {
			mAmPmTextView = (TextView) parent.findViewById(R.id.ampm);
			String[] ampm = new DateFormatSymbols().getAmPmStrings();
			mAmString = ampm[0];
			mPmString = ampm[1];
		}

		void setShowAmPm(boolean show) {
			if (mAmPmTextView != null) {
				mAmPmTextView.setVisibility(show ? View.VISIBLE : View.GONE);
			}
		}

		void setIsMorning(boolean isMorning) {
			if (mAmPmTextView != null) {
				mAmPmTextView.setText(isMorning ? mAmString : mPmString);
			}
		}
	}

	/*ʱ��ˢ���漰��View�ĸ�����ʾ(�ر���ÿ��ˢ����ʾ��������Ƶ���ر��)����Ȼ���˴���ʱ����ʾ��ÿ���Ӹ���һ��
	 * �����ڼ���ʱ����µĹ㲥�в��������ã���ֹ�ڲ���ˢ�µ�ǰ����Viewʱ�����ڴ�й¶
	 */
	private static class TimeChangedReceiver extends BroadcastReceiver {
		
		//����������
		private WeakReference<DigitalClock> mClock;
		private Context mContext;

		public TimeChangedReceiver(DigitalClock clock) {
			mClock = new WeakReference<DigitalClock>(clock);
			mContext = clock.getContext();
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			
			// Post a runnable to avoid blocking the broadcast.
			final boolean timezoneChanged = intent.getAction().equals(
					Intent.ACTION_TIMEZONE_CHANGED);
			//���������л�ȡ����
			final DigitalClock clock = mClock.get();
			if (clock != null) {
				clock.mHandler.post(new Runnable() {
					public void run() {
						if (timezoneChanged) {
							clock.mCalendar = Calendar.getInstance();
						}
						clock.updateTime();
					}
				});
			} else {
				try {
					mContext.unregisterReceiver(this);
				} catch (RuntimeException e) {
					// Shouldn't happen
				}
			}
		}
	};

	// ����ʱ����ʾ�ĸ�ʽ�ı�
	private static class FormatChangeObserver extends ContentObserver {
		// ������Ӧ��
		private WeakReference<DigitalClock> mClock;
		private Context mContext;

		public FormatChangeObserver(DigitalClock clock) {
			super(new Handler());
			mClock = new WeakReference<DigitalClock>(clock);
			mContext = clock.getContext();
		}

		@Override
		public void onChange(boolean selfChange) {
			DigitalClock digitalClock = mClock.get();
			//����������ȡ������
			if (digitalClock != null) {
				//������������ȡ���Ķ������ʱ�����
				digitalClock.setDateFormat();
				digitalClock.updateTime();
			} else {
				try {
					mContext.getContentResolver().unregisterContentObserver(
							this);
				} catch (RuntimeException e) {
					// Shouldn't happen
				}
			}
		}
	}

	// ����ʱ��
	private void updateTime() {
		Toast.makeText(mContext, "updateTime", Toast.LENGTH_SHORT).show();
		mCalendar.setTimeInMillis(System.currentTimeMillis());

		CharSequence newTime = DateFormat.format(mFormat, mCalendar);
		mDislpayTime.setText(newTime);
		mAmPm.setIsMorning(mCalendar.get(Calendar.AM_PM) == 0);
	}

	private void setDateFormat() {
		// ��ȡʱ����
		mFormat = android.text.format.DateFormat.is24HourFormat(getContext()) ? M24
				: M12;
		// ����ʱ������ʾ���硢����
		mAmPm.setShowAmPm(mFormat.equals(M12));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		//Toast.makeText(mContext, "ddd", Toast.LENGTH_SHORT).show();
	}
}
