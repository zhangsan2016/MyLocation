package smartcity.ldgd.com.mylocation;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import smartcity.ldgd.com.mylocation.type.User;
import smartcity.ldgd.com.mylocation.type.UserJson;
import smartcity.ldgd.com.mylocation.util.HttpUtil;
import smartcity.ldgd.com.mylocation.util.LogUtil;
import smartcity.ldgd.com.mylocation.util.NetUtils;
import smartcity.ldgd.com.mylocation.util.SharedPreferencesUtil;

import static smartcity.ldgd.com.mylocation.R.id.map;

public class MainActivity extends AppCompatActivity implements AMap.OnCameraChangeListener, GeocodeSearch.OnGeocodeSearchListener, AMap.OnMapClickListener, AMap.OnMapTouchListener {
    public static final String USER_INFO = "USER_INFO";
    private List<String> list = new ArrayList<String>();

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //  当前定位位置
    private AMapLocation mAMapLocation = null;
    private AlertDialog alarmDialog;

    private MapView mMapView = null;
    private AMap mAMap = null;
    private UiSettings mUiSettings;
    private Marker marker = null;
    private Marker moveMarker = null;
    private MarkerOptions markerOption;
    private MarkerOptions moveMarkerOption;
    // 四个按钮
    private LinearLayout llUserInfo, ll_alarm, ll_call;
    private User currentUser = null;
    // 用户拖动地图后，不再跟随移动，需要跟随移动时再把这个改成true
    private boolean followMove = true;
    private GeocodeSearch geocoderSearch;
    // 移动或者定位所获得的地址
    private String addressName;
    private ImageView iv_reposition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

            //设置修改状态栏
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            //设置状态栏的颜色，和你的app主题或者标题栏颜色设置一致就ok了
            window.setStatusBarColor(getResources().getColor(R.color.colorGreen));

            if (actionBar != null) {
                actionBar.hide();
            }
        }
        setContentView(R.layout.activity_main);

        initView(savedInstanceState);

        // 检查用户信息是否已经录入
        checkUserInfo();


        // 检测网络状态
        initNet();

        // 初始化地图
        initMap();

        // 请求权限
        requestPermission();

        // 初始化定位
        initLocation();



      /*  //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();*/


    }

    private void checkUserInfo() {
        SharedPreferencesUtil.getInstance(this, "ldgd");
        String userInfo = (String) SharedPreferencesUtil.getData(USER_INFO, "");

        if (userInfo.equals("")) {
            // 显示用户信息录入界面
            startSaveUser();
        } else {
            // 初始化 Gson
            Gson gson = new Gson();
            currentUser = gson.fromJson(userInfo, User.class);
        }

    }

    private void startSaveUser() {
        Intent intent = new Intent(this, SaveUserInfoActivity.class);
        startActivity(intent);
    }

    private void initView(Bundle savedInstanceState) {
        //获取地图控件引用
        mMapView = (MapView) findViewById(map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        llUserInfo = (LinearLayout) this.findViewById(R.id.ll_user_info);
        ll_call = (LinearLayout) this.findViewById(R.id.ll_call);
        ll_alarm = (LinearLayout) this.findViewById(R.id.ll_alarm);
        iv_reposition = (ImageView) this.findViewById(R.id.iv_reposition);

        list.add("杂类");
        list.add("气体");
        list.add("爆炸品");
        list.add("易燃液体");
        list.add("易燃固体");
        list.add("氧化剂");
        list.add("毒害品");
        list.add("放射性物品");
        list.add("腐蚀品");


        llUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 用户信息
                Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                startActivity(intent);
            }
        });
        ll_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 联系中心
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "110"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
        ll_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 隐藏 InfoWindow
                moveMarker.hideInfoWindow();
                // 报警
                final String code = randomCode();
                View view = View.inflate(MainActivity.this, R.layout.alarm_verification_item, null);
                TextView tvCode = view.findViewById(R.id.tv_code);
                final EditText edWriteCode = view.findViewById(R.id.ed_write_code);
                tvCode.setText(code.replace("", " ").trim());
                alarmDialog = new AlertDialog.Builder(MainActivity.this).setTitle("请输入验证码")
                        .setView(view)
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create();


                alarmDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button btnPositive = alarmDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        btnPositive.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String edCode = edWriteCode.getText().toString().trim();
                                if (edCode.equals(code)) {


                                    // 隐藏 InfoWindow
                                    moveMarker.hideInfoWindow();
                                    // 发送报警信息
                                    sendAlarm();

                                } else {
                                    showToast("验证码输入错误！");
                                }
                            }
                        });
                    }
                });
                alarmDialog.show();
            }
        });

        iv_reposition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAMapLocation != null) {
                    LatLng latLng = new LatLng(mAMapLocation.getLatitude(), mAMapLocation.getLongitude());
                    marker.setPosition(latLng);
                    marker.showInfoWindow();
                    moveMarker.setPosition(latLng);
                    moveMarker.setVisible(false);
                    mAMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                }

            }
        });

    }

    private void sendAlarm() {

        if (marker == null) {
            showToast("获取定位失败");
            return;
        }

        showProgress();
        new Thread(new Runnable() {
            @Override
            public void run() {

                String url = "https://iot2.sz-luoding.com:888/api/vehicle_alarm/new";

                Gson gson = new Gson();
                String userInfo = (String) SharedPreferencesUtil.getData(USER_INFO, "");
                currentUser = gson.fromJson(userInfo, User.class);

                //创建json
                LatLng latLng = moveMarker.getPosition();
                UserJson userJson = new UserJson(currentUser.getCarNumber(), Double.toString(latLng.longitude), Double.toString(latLng.latitude));
                userJson.setAddress(addressName);
                userJson.setContact(currentUser.getPhone());
                userJson.setCompany(currentUser.getShipperCompany());
                userJson.setCustomer(currentUser.getReceivingCompany());
                userJson.setDangerType(list.get(currentUser.getTypeNub()));


                LogUtil.e("xxx" + userJson);

                // 创建请求的参数body
                //   String postBody = "{\"where\":{\"PROJECT\":" + title + "},\"size\":5000}";
                String postBody = new Gson().toJson(userJson);
                RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), postBody);

                LogUtil.e("postBody = " + postBody);


                HttpUtil.sendSookiePostHttpRequest(url, new Callback() {

                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        showToast("连接服务器失败！");
                        stopProgress();
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, Response response) throws IOException {
                        try {

                            String json = response.body().string();
                            LogUtil.e("json = " + json);


                            stopProgress();
                            alarmDialog.cancel();
                            showToast("报警成功");

                           /* // 解析返回过来的json
                            Gson gson = new Gson();
                            LoginJson loginInfo = gson.fromJson(json, LoginJson.class);


                            if (loginInfo.getErrno() == 0) {

                                reportDevice(xmlConfig, uuid, loginInfo.getData().getToken().getToken());

                            } else {
                                showToast("连接服务器失败！");
                            }*/
                        } catch (Exception e) {
                            e.printStackTrace();
                            stopProgress();
                            showToast("获取异常错误 ：" + e.getMessage());
                        }

                    }
                }, requestBody);

            }
        }).start();

    }

    private String randomCode() {
        String strRand = "";
        for (int i = 0; i < 4; i++) {
            strRand += String.valueOf((int) (Math.random() * 10));
        }
        return strRand;
    }

    private void showToast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT);
                toast.setText(str);
                toast.show();

            }
        });

    }

    protected ProgressDialog mProgress;

    protected void showProgress() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress = ProgressDialog.show(MainActivity.this, "", "请稍等...");
            }
        });

    }

    private void stopProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress.cancel();
            }
        });
    }

    private void initMap() {
        if (mAMap == null) {
            // 初始化地图
            mAMap = mMapView.getMap();
            mUiSettings = mAMap.getUiSettings();
            //  mAMap.setOnMapLoadedListener(this);
            // 设置地图样式
            //  setMapCustomStyleFile(this);
            // 设置地图logo显示在右下方
            mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
            // 设置地图默认的缩放按钮是否显示
            mUiSettings.setZoomControlsEnabled(false);
            // 设置地图缩放比例
            mAMap.moveCamera(CameraUpdateFactory.zoomTo(5f));
            //  对amap添加移动地图事件监听器
            mAMap.setOnCameraChangeListener(this);
            // 设置地图点监听击事件
            mAMap.setOnMapClickListener(this);
            // 设置地图触摸监听事件
            mAMap.setOnMapTouchListener(this);
            //返回地址详细信息代码
            geocoderSearch = new GeocodeSearch(this);
            geocoderSearch.setOnGeocodeSearchListener(this);


            // 添加覆盖物
        /*    markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark2))
                    .position(new LatLng(22.493403, 114.10998))
                    .draggable(true);
            marker = mAMap.addMarker(markerOption);*/

        }
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stopLocation();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }


    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        } else {
            //开始定位
            //   Toast.makeText(MainActivity.this,"已开启定位权限",Toast.LENGTH_LONG).show();
        }
    }

    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {

                        mAMapLocation = amapLocation;

                     /*   //定位成功回调信息，设置相关消息
                        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表

                        amapLocation.getAccuracy();//获取精度信息
                        amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                        amapLocation.getCountry();//国家信息
                        amapLocation.getProvince();//省信息
                        amapLocation.getCity();//城市信息
                        amapLocation.getDistrict();//城区信息
                        amapLocation.getStreet();//街道信息
                        amapLocation.getStreetNum();//街道门牌号信息
                        amapLocation.getCityCode();//城市编码
                        amapLocation.getAdCode();//地区编码
                        amapLocation.getAoiName();//获取当前定位点的AOI信息

                        Log.e("sss", "xxx 当前位置（经纬度） = " + amapLocation.getLatitude() + ":" + amapLocation.getLongitude() + " 当前位置在：" + amapLocation.getAddress());*/

                        // 设置覆盖物

                        if (followMove) {
                            if (mAMap != null) {
                                mAMap.clear();
                            }

                            // 保存定位地址，用于报警
                            addressName = amapLocation.getDistrict() + amapLocation.getStreet() + amapLocation.getAoiName() + amapLocation.getStreetNum();

                            // 设置定位点覆盖物
                            String address = amapLocation.getDistrict() + amapLocation.getStreet() + amapLocation.getAoiName() + amapLocation.getStreetNum();
                            markerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark2))
                                    .title("当前位置").snippet(address).position(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()))
                                    .draggable(false);
                            marker = mAMap.addMarker(markerOption);
                            marker.showInfoWindow();

                            // 设置可移动覆盖物
                            // 设置定位点覆盖物
                            String moveAddress = amapLocation.getLatitude() + "," + amapLocation.getLongitude();
                            moveMarkerOption = new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_openmap_mark3))
                                    .title("当前位置").snippet(moveAddress).position(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()))
                                    .draggable(true);
                            moveMarker = mAMap.addMarker(moveMarkerOption);
                            //  moveMarker.setAlpha(0);
                            moveMarker.setVisible(false);
                            moveMarker.hideInfoWindow();

                            followMove = false;

                               /*    if (followMove) {
                            mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng));
                        }*/
                            // 设置地图中心点
                            mAMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()), 16, 0, 0)));
                        }

                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                    }
                }

            }
        });

        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        //   mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        AMapLocationClientOption option = new AMapLocationClientOption();
        /**
         * 设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
         */
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        if (null != mLocationClient) {
            mLocationClient.setLocationOption(option);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        /*         mLocationClient.stopLocation();
            mLocationClient.startLocation();*/
        }

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.stopLocation();
        mLocationClient.startLocation();
    }


    public void starLocation(View view) {
       /* //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.stopLocation();
        mLocationClient.startLocation();*/


    }


    private void initNet() {

        boolean connected = NetUtils.isConnected(MainActivity.this);
        if (connected) {
            boolean wifi = NetUtils.isWifi(MainActivity.this);
            boolean rd = NetUtils.is3rd(MainActivity.this);
            if (wifi) {
                // Toast.makeText(MainActivity.this, "WIFI已经连接", Toast.LENGTH_SHORT).show();
            } else if (rd) {
                // Toast.makeText(MainActivity.this, "手机流量已经连接", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "网络连接不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
                NetUtils.openSetting(MainActivity.this);
            }
        } else {
            Toast.makeText(MainActivity.this, "网络连接不可用，请检查网络设置", Toast.LENGTH_SHORT).show();
            NetUtils.openSetting(MainActivity.this);
        }
    }

    /**
     * 响应逆地理编码
     */
    public void getAddress(final LatLonPoint latLonPoint) {
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,
                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocoderSearch.getFromLocationAsyn(query);// 设置异步逆地理编码请求
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "未开启定位权限，请手动到设置去开去权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        LatLng latLng = cameraPosition.target;
        LogUtil.e("cameraPosition = " + cameraPosition.toString());

        if (markerOption != null) {
            // moveMarker.setPosition(cameraPosition.target);
            //    moveMarker.setSnippet(latLng.toString());
            moveMarker.setPosition(new LatLng(latLng.latitude, latLng.longitude));


        }


    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        //根据latLng编译成地理描述
        LatLng latLng = cameraPosition.target;
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        getAddress(latLonPoint);

    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {

        if (rCode == 1000) {

            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null) {

                addressName = result.getRegeocodeAddress().getFormatAddress();
                moveMarker.setSnippet(addressName);
                moveMarker.showInfoWindow();


            } else {

            }
        } else {

        }

    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

    }

    @Override
    public void onMapClick(LatLng latLng) {

    }

    @Override
    public void onTouch(MotionEvent motionEvent) {
        if (moveMarker != null) {
            moveMarker.setVisible(true);
            marker.hideInfoWindow();
        }
    }
}
