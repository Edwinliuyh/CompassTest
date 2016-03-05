package com.example.compasstest;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private SensorManager sensorManager;
	private ImageView compassImg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		compassImg = (ImageView) findViewById(R.id.compass_img);
		//分别获取到加速度传感器和地磁传感器，注册监听器
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor magneticSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		Sensor accelerometerSensor = sensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(listener, magneticSensor,
				SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(listener, accelerometerSensor,
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (sensorManager != null) {
			sensorManager.unregisterListener(listener);
		}
	}

	private SensorEventListener listener = new SensorEventListener() {

		float[] accelerometerValues = new float[3];
		float[] magneticValues = new float[3];
		private float lastRotateDegree;

		@Override
		public void onSensorChanged(SensorEvent event) {
			// 判断当前是加速度传感器还是地磁传感器
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				// 注意赋值时要调用clone()方法
				accelerometerValues = event.values.clone();
			} else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
				// 注意赋值时要调用clone()方法
				magneticValues = event.values.clone();
			}

			//getRotationMatrix()方法得到一个包含旋转矩阵的R 数组
			//第一个参数R getRotationMatrix()方法计算出的旋转数据就会赋值到这个数组当中。
			//第二个参数是一个用于将地磁向量转换成重力坐标的旋转矩阵，通常指定为null 即可。
			//第三和第四个参数则分别就是加速度传感器和地磁传感器输出的values 值。
			float[] R = new float[9];
			SensorManager.getRotationMatrix(R, null, accelerometerValues,
					magneticValues);
			//getOrientation()方法得到包含旋转数据的Values数组
			//values[0]记录着手机围绕着Z轴的旋转弧度，values[1]记录着手机围绕X 轴的旋转弧度，
			//values[2]记录着手机围绕Y 轴的旋转弧度
			float[] values = new float[3];
			SensorManager.getOrientation(R, values);
			//算出的数据都是以弧度为单位的，因此如果你想将它们转换成角度
			float rotateDegree = -(float) Math.toDegrees(values[0]);


			if (Math.abs(rotateDegree - lastRotateDegree) > 1) {
				//执行旋转动画
				RotateAnimation animation = new RotateAnimation(
						lastRotateDegree, rotateDegree,
						Animation.RELATIVE_TO_SELF, 0.5f,
						Animation.RELATIVE_TO_SELF, 0.5f);
				animation.setFillAfter(true);
				compassImg.startAnimation(animation);
				lastRotateDegree = rotateDegree;
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

	};

}
