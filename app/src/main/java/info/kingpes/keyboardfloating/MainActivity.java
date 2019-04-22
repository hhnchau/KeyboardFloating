package info.kingpes.keyboardfloating;

import android.content.res.Resources;
import android.inputmethodservice.Keyboard;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        final MyKeyboardFloating myKeyboardFloating = findViewById(R.id.keyboardview);
//        myKeyboardFloating.setKeyboard(new Keyboard(this, R.xml.numpad));
//        myKeyboardFloating.setPreviewEnabled(false);
//        myKeyboardFloating.registerEditText(R.id.edt);
//        myKeyboardFloating.registerEditText(R.id.edt1);
//        myKeyboardFloating.setAlignBottomCenter(true);
//
//
//        findViewById(R.id.edt).setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                myKeyboardFloating.show();
//            }
//        });
//
//        findViewById(R.id.edt1).setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                myKeyboardFloating.show();
//            }
//        });
//
//        findViewById(R.id.edt).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myKeyboardFloating.hide();
//            }
//        });
//
//
//        findViewById(R.id.edt1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myKeyboardFloating.hide();
//            }
//        });


        final EditText e = findViewById(R.id.edt1);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, (int) e.getText().charAt(0) + "", Toast.LENGTH_SHORT).show();
            }
        });

        MyKeyboard myKeyboard = findViewById(R.id.myKeyboard);
        myKeyboard.setKeyboard(new Keyboard(this, R.xml.fullpad));
        myKeyboard.setPreviewEnabled(false);
        myKeyboard.registerEditText(R.id.edt);

    }

//    @Override public void onBackPressed() {
//        if( mCustomKeyboard.isCustomKeyboardVisible() ) mCustomKeyboard.hideCustomKeyboard(); else this.finish();
//    }
}
