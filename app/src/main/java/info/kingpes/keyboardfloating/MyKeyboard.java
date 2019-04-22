package info.kingpes.keyboardfloating;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;

public class MyKeyboard extends KeyboardView {

    public MyKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {
            final static int CodeDelete = -5; // Keyboard.KEYCODE_DELETE
            final static int CodeCancel = -3; // Keyboard.KEYCODE_CANCEL
            final static int CodePrev = 55000;
            final static int CodeAllLeft = 55001;
            final static int CodeLeft = 55002;
            final static int CodeRight = 55003;
            final static int CodeAllRight = 55004;
            final static int CodeNext = 55005;
            final static int CodeClear = 55006;

            @SuppressWarnings("ResourceType")
            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
                View focusCurrent = ((Activity) getContext()).getWindow().getCurrentFocus();
                if (focusCurrent == null || (focusCurrent.getClass() != EditText.class
                        && focusCurrent.getClass().getSuperclass() != EditText.class)) return;
                EditText edittext = (EditText) focusCurrent;
                Editable editable = edittext.getText();
                int start = edittext.getSelectionStart();
                int end = edittext.getSelectionEnd();
                // Apply the key to the edittext
                if (primaryCode == CodeCancel) {
                    hide();
                } else if (primaryCode == CodeDelete) {
                    if (editable != null && start > 0) {
                        editable.delete(start - 1, start);
                    } else if (editable != null && start != end) { // delete selection
                        editable.delete(start, end);
                    }
                } else if (primaryCode == CodeClear) {
                    if (editable != null) editable.clear();
                } else if (primaryCode == CodeLeft) {
                    if (start > 0) edittext.setSelection(start - 1);
                } else if (primaryCode == CodeRight) {
                    if (start < edittext.length()) edittext.setSelection(start + 1);
                } else if (primaryCode == CodeAllLeft) {
                    edittext.setSelection(0);
                } else if (primaryCode == CodeAllRight) {
                    edittext.setSelection(edittext.length());
                } else if (primaryCode == CodePrev) {
                    View focusNew = edittext.focusSearch(View.FOCUS_BACKWARD);
                    if (focusNew != null) focusNew.requestFocus();
                } else if (primaryCode == CodeNext) {
                    View focusNew = edittext.focusSearch(View.FOCUS_FORWARD);
                    if (focusNew != null) focusNew.requestFocus();
                } else { // insert character
                    if (start != end) {
                        editable.delete(start, end);
                    }
                    editable.insert(start, Character.toString((char) primaryCode));
                }
            }

            @Override
            public void onPress(int arg0) {
            }

            @Override
            public void onRelease(int primaryCode) {
            }

            @Override
            public void onText(CharSequence text) {
            }

            @Override
            public void swipeDown() {
            }

            @Override
            public void swipeLeft() {
            }

            @Override
            public void swipeRight() {
            }

            @Override
            public void swipeUp() {
            }
        };
        this.setOnKeyboardActionListener(mOnKeyboardActionListener);
        ((Activity) getContext()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


    public boolean isVisible() {
        return this.getVisibility() == View.VISIBLE;
    }

    public void show(View v) {
        this.setVisibility(View.VISIBLE);
        this.setEnabled(true);
        if (v != null)
            ((InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void hide() {
        this.setVisibility(View.GONE);
        this.setEnabled(false);
    }

    public void registerEditText(int resId) {
        EditText edittext = ((Activity) getContext()).findViewById(resId);
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) show(v);
                else hide();
            }
        });
        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show(v);
            }
        });

        // Disable standard keyboard hard way
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            edittext.setShowSoftInputOnFocus(false);
        } else {
            //For sdk versions [14-20]
            try {
                final Method method = EditText.class.getMethod(
                        "setShowSoftInputOnFocus"
                        , boolean.class);
                method.setAccessible(true);
                method.invoke(edittext, false);
            } catch (Exception e) {
                // ignore
            }
        }
        edittext.setInputType(edittext.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
            RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
            relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            setLayoutParams(relativeLayoutParams);
    }
}
