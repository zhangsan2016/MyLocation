package smartcity.ldgd.com.mylocation;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import smartcity.ldgd.com.mylocation.type.User;
import smartcity.ldgd.com.mylocation.util.LogUtil;
import smartcity.ldgd.com.mylocation.util.SharedPreferencesUtil;

import static smartcity.ldgd.com.mylocation.MainActivity.USER_INFO;

public class UserInfoActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<String>();
    private Spinner sp_type;
    private EditText et_car_number, et_phone, et_shipper_company, et_receiving_company;
    private Button btAddUser;
    // 选中的类型号
    private String spinnerTypeNub;

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
        setContentView(R.layout.activity_user_info);

        initView();

        intitListener();


    }

    private void intitListener() {
        //添加Spinner事件监听
        sp_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerTypeNub = list.get(position);
                //设置显示当前选择的项
                parent.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 显示加载框
                showProgress();

                String carNumber = et_car_number.getText().toString().trim();
                int typeNub = sp_type.getSelectedItemPosition();
                String phone = et_phone.getText().toString().trim();
                String shipperCompany = et_shipper_company.getText().toString().trim();
                String receivingCompany = et_receiving_company.getText().toString().trim();

                if (carNumber.equals("")) {
                    showToast("车牌号不能为空");
                    // 关闭加载框
                    stopProgress();
                    return;
                } else if (phone.equals("")) {
                    showToast("手机号不能为空");
                    // 关闭加载框
                    stopProgress();
                    return;
                }

                User user = new User();
                user.setCarNumber(carNumber);
                user.setTypeNub(typeNub);
                user.setPhone(phone);
                user.setShipperCompany(shipperCompany);
                user.setReceivingCompany(receivingCompany);

                LogUtil.e("xxx user = " + user.toString());

                SharedPreferencesUtil.putData(USER_INFO, user);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                        // 关闭加载框
                        stopProgress();
                        showToast("修改成功");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            }
        });

    }

    private void showToast(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(UserInfoActivity.this, str, Toast.LENGTH_SHORT);
                toast.setText(str);
                toast.show();
            }
        });

    }

    private void initView() {
        sp_type = (Spinner) this.findViewById(R.id.sp_type);
        et_car_number = (EditText) this.findViewById(R.id.et_car_number);
        et_phone = (EditText) this.findViewById(R.id.et_phone);
        et_shipper_company = (EditText) this.findViewById(R.id.et_shipper_company);
        et_receiving_company = (EditText) this.findViewById(R.id.et_receiving_company);
        btAddUser = (Button) this.findViewById(R.id.bt_add_user);


        initSpinner();

        // 初始化个人信息
        initUserInfo();


    }

    private void initUserInfo() {
        //  SharedPreferencesUtil.getInstance(this, "ldgd");
        String userInfo = (String) SharedPreferencesUtil.getData(USER_INFO, "");
        // 初始化 Gson
        Gson gson = new Gson();
        User currentUser = gson.fromJson(userInfo, User.class);

        if(currentUser != null){
            et_car_number.setText(currentUser.getCarNumber() + "");
            et_phone.setText(currentUser.getPhone() + "");
            et_receiving_company.setText(currentUser.getReceivingCompany() + "");
            et_shipper_company.setText(currentUser.getShipperCompany() + "");
            sp_type.setSelection(currentUser.getTypeNub());
        }


    }

    private void initSpinner() {
        list.add("杂类");
        list.add("气体");
        list.add("爆炸品");
        list.add("易燃液体");
        list.add("易燃固体");
        list.add("氧化剂");
        list.add("毒害品");
        list.add("放射性物品");
        list.add("腐蚀品");

        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(UserInfoActivity.this, android.R.layout.simple_spinner_item, list);
        sp_type.setAdapter(arrayAdapter);
    }

    public void clear(View view) {
        this.finish();
    }

    protected ProgressDialog mProgress;

    protected void showProgress() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgress = ProgressDialog.show(UserInfoActivity.this, "", "请稍等...");
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
}
