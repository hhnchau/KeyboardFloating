package info.kingpes.keyboardfloating;

import android.inputmethodservice.Keyboard;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MyKeyboardFloating myKeyboardFloating = findViewById(R.id.keyboardview);
        myKeyboardFloating.setKeyboard(new Keyboard(this, R.xml.fullpad));
        myKeyboardFloating.setPreviewEnabled(false);
        myKeyboardFloating.registerEditText(R.id.edt);
        myKeyboardFloating.registerEditText(R.id.edt1);
        myKeyboardFloating.setAlignBottomCenter(true);


        findViewById(R.id.edt).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                myKeyboardFloating.show(v);
            }
        });

        findViewById(R.id.edt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myKeyboardFloating.show(v);
            }
        });


        findViewById(R.id.edt1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myKeyboardFloating.hide();
            }
        });

    }
}
