package com.example.dixon.bluetoothcar;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public Button mBT1;
    public ListView mLV1;

    public ArrayList<String> list1=new ArrayList<>();
    public ArrayAdapter<String> adapter1;
    public String msg;
    public Button mS1;
    public Button mS2;
    public Button mS3;
    public Button mS4;
    public Button mS5;

    BluetoothAdapter mBA=BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket mSocket;
    OutputStream os;
    /** unregister broadcast **/
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    //    两个组件
    private TextureView textureView;
    //    摄像头ID，一般0是后视，1是前视
    private String cameraId;
    //定义代表摄像头的成员变量，代表系统摄像头，该类的功能类似早期的Camera类。
    protected CameraDevice cameraDevice;
    //    定义CameraCaptureSession成员变量，是一个拍摄绘话的类，用来从摄像头拍摄图像或是重新拍摄图像,这是一个重要的API.
    protected CameraCaptureSession cameraCaptureSessions;
    //    当程序调用setRepeatingRequest()方法进行预览时，或调用capture()进行拍照时，都需要传入CaptureRequest参数时
//    captureRequest代表一次捕获请求，用于描述捕获图片的各种参数设置。比如对焦模式，曝光模式...等，程序对照片所做的各种控制，都通过CaptureRequest参数来进行设置
//    CaptureRequest.Builder 负责生成captureRequest对象
    protected CaptureRequest.Builder captureRequestBuilder;
    //预览尺寸
    private Size imageDimension;
    //请求码常量，可以自定义
    private static final int REQUEST_CAMERA_PERMISSION = 300;
    private Handler mBackgroundHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mS1=findViewById(R.id.s1);
        mS1.setOnClickListener(this);
        mS2=findViewById(R.id.s2);
        mS2.setOnClickListener(this);
        mS3=findViewById(R.id.s3);
        mS3.setOnClickListener(this);
        mS4=findViewById(R.id.s4);
        mS4.setOnClickListener(this);
        mS5=findViewById(R.id.s5);
        mS5.setOnClickListener(this);

        mBT1=findViewById(R.id.b1);
        mLV1=findViewById(R.id.lv1);
        adapter1=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,list1);
        mLV1.setAdapter(adapter1);
        /** check bluetooth function and get bonded devices **/
        if(mBA!=null){
            if(mBA.isEnabled()){
                list1.clear();
                Set<BluetoothDevice> pairedDevice=mBA.getBondedDevices();
                if(pairedDevice.size()>0){
                    for(BluetoothDevice device:pairedDevice){
                        String name=device.getName();
                        String address=device.getAddress();
                        String str=name+"="+address;
                        list1.add(str);
                        adapter1.notifyDataSetChanged();
                    }
                }
            }else {
                Intent BTenable=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivity(BTenable);
            }

        }
        else {
            Toast.makeText(MainActivity.this,"此设备不支持蓝牙！！！",Toast.LENGTH_LONG).show();
        }
        /**connect bundled bluetooth **/
        mLV1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String s=adapter1.getItem(position);
                String address=s.substring(s.indexOf("=")+1).trim();
                BluetoothDevice device=mBA.getRemoteDevice(address);
                UUID uuid=UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
                try{
                    mSocket=device.createRfcommSocketToServiceRecord(uuid);
                    mSocket.connect();
                    os=mSocket.getOutputStream();
                }catch (IOException e){
                    e.printStackTrace();
                }
                if(mSocket.isConnected()){
                    mS1.setEnabled(true);
                    mS2.setEnabled(true);
                    mS3.setEnabled(true);
                    mS4.setEnabled(true);
                    mS5.setEnabled(true);
                    mBT1.setVisibility(View.GONE);
                    mLV1.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this,"连接完成!!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        textureView = findViewById(R.id.texture);
        assert textureView != null;
//        设置监听
        textureView.setSurfaceTextureListener(textureListener);
    }

    /** send message **/
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.s1:
                msg = "1";
                try {
                    os.write(msg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.s2:
                msg = "2";
                try {
                    os.write(msg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.s3:
                msg = "3";
                try {
                    os.write(msg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.s4:
                msg = "4";
                try {
                    os.write(msg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.s5:
                msg = "5";
                try {
                    os.write(msg.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    //    定义了一个独立的监听类
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return true;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
//        摄像头打开激发该方法
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
//            开始预览
            createCameraPreview();
        }
        //        摄像头断开连接时的方法
        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
            MainActivity.this.cameraDevice = null;
        }
        //        打开摄像头出现错误时激发方法
        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            MainActivity.this.cameraDevice = null;
        }
    };
    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
//            设置默认的预览大小
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
//            请求预览
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
//            创建cameraCaptureSession,第一个参数是图片集合，封装了所有图片surface,第二个参数用来监听这处创建过程
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void openCamera() {
//        实例化摄像头
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
//            指定要打开的摄像头
            cameraId = manager.getCameraIdList()[0];
//            获取打开摄像头的属性
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
//The available stream configurations that this camera device supports; also includes the minimum frame durations and the stall durations for each format/size combination.
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
//            权限检查
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
//            打开摄像头，第一个参数代表要打开的摄像头，第二个参数用于监测打开摄像头的当前状态，第三个参数表示执行callback的Handler,
//            如果程序希望在当前线程中执行callback，像下面的设置为null即可。
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    protected void updatePreview() {

//        设置模式为自动
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {

            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
