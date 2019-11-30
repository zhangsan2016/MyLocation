package smartcity.ldgd.com.mylocation;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class SaveUserInfoActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<String>();
    private Spinner sp_type;
    private EditText et_car_number,et_phone,et_shipper_company,et_receiving_company;
    private Button btAddUser;

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
        setContentView(R.layout.activity_save_user_info);

        initView();


    }

    private void initView() {
        sp_type = (Spinner) this.findViewById(R.id.sp_type);
        et_car_number = (EditText) this.findViewById(R.id.et_car_number);
        et_phone = (EditText) this.findViewById(R.id.et_phone);
        et_shipper_company = (EditText) this.findViewById(R.id.et_shipper_company);
        et_receiving_company = (EditText) this.findViewById(R.id.et_receiving_company);
        btAddUser = (Button) this.findViewById(R.id.bt_add_user);


        initSpinner();


    }

    private void initSpinner() {
        list.add("爆炸品");
        list.add("气体");
        list.add("易燃液体");
        list.add("易燃固体");
        list.add("氧化剂");
        list.add("毒害品");
        list.add("放射性物品");
        list.add("腐蚀品");
        list.add("杂类");
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(SaveUserInfoActivity.this, android.R.layout.simple_spinner_item, list);
        sp_type.setAdapter(arrayAdapter);
    }

    public void clear(View view) {
        this.finish();
    }
}
