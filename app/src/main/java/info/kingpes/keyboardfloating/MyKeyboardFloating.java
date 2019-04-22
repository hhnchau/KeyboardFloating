package info.kingpes.keyboardfloating;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import java.lang.reflect.Method;

public class MyKeyboardFloating extends KeyboardView {
    private static final int MOVE_THRESHOLD = 0;
    private static final int TOP_PADDING_DP = 28;
    private static final int HANDLE_COLOR = Color.parseColor("#AAD1D6D9");
    private static final int HANDLE_PRESSED_COLOR = Color.parseColor("#D1D6D9");
    private static final float HANDLE_ROUND_RADIOUS = 20.0f;
    private static final CornerPathEffect HANDLE_CORNER_EFFECT = new CornerPathEffect(HANDLE_ROUND_RADIOUS);
    private static int topPaddingPx;
    private static int width;
    private static Path mHandlePath;
    private static Paint mHandlePaint;
    private static boolean allignBottomCenter = false;

    private int resids;

    public MyKeyboardFloating(Context context, AttributeSet attrs) {
        super(context, attrs);

        topPaddingPx = (int) convertDpToPixel((float) TOP_PADDING_DP, context);
        // insert character
        OnKeyboardActionListener mOnKeyboardActionListener = new OnKeyboardActionListener() {
            final static int CodeGrab = -10; //
            final static int CodeDelete = -5; // Keyboard.KEYCODE_DELETE
            final static int CodeCancel = -3; // Keyboard.KEYCODE_CANCEL
            final static int CodePrev = 55000;
            final static int CodeAllLeft = 55001;
            final static int CodeLeft = 55002;
            final static int CodeRight = 55003;
            final static int CodeAllRight = 55004;
            final static int CodeNext = 55005;
            final static int CodeClear = 55006;

            final static int CodeCellUp = 1001;
            final static int CodeCellDown = 1002;
            final static int CodeCellLeft = 1003;
            final static int CodeCellRight = 1004;
            final static int CodeDecimalPoint = 46;
            final static int CodeZero = 48;

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
                    else if (primaryCode == CodeCellUp || primaryCode == CodeCellDown || primaryCode == CodeCellLeft || primaryCode == CodeCellRight) {
                        int i =0;
                    } else if (primaryCode == CodeGrab) {
                        int i = 1;
                    }
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

        OnTouchListener mKeyboardOntTouchListener = new OnTouchListener() {
            float dx;
            float dy;
            int moveToY;
            int moveToX;
            int distY;
            int distX;
            Rect inScreenCoordinates;
            boolean handleTouched = false;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                // Use ViewGroup.MarginLayoutParams so as to work inside any layout
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                boolean performClick = performClick();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        if (handleTouched) {
                            moveToY = (int) (event.getRawY() - dy);
                            moveToX = (int) (event.getRawX() - dx);
                            distY = moveToY - params.topMargin;
                            distX = moveToX - params.leftMargin;

                            if (Math.abs(distY) > MOVE_THRESHOLD ||
                                    Math.abs(distX) > MOVE_THRESHOLD) {
                                // Ignore any distance before threshold reached
                                moveToY = moveToY - Integer.signum(distY) * Math.min(MOVE_THRESHOLD, Math.abs(distY));
                                moveToX = moveToX - Integer.signum(distX) * Math.min(MOVE_THRESHOLD, Math.abs(distX));

                                inScreenCoordinates = keepInScreen(moveToY, moveToX);
                                view.setY(inScreenCoordinates.top);
                                view.setX(inScreenCoordinates.left);
                            }
                            performClick = false;
                        } else {
                            performClick = true;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (handleTouched) {
                            // reset handle color
                            mHandlePaint.setColor(HANDLE_COLOR);
                            mHandlePaint.setStyle(Paint.Style.FILL);
                            invalidate();

                            performClick = false;
                        } else {
                            performClick = true;
                        }

                        break;

                    case MotionEvent.ACTION_DOWN:
                        handleTouched = event.getY() <= getPaddingTop(); // Allow move only wher touch on top padding
                        dy = event.getRawY() - view.getY();
                        dx = event.getRawX() - view.getX();

                        //change handle color on tap
                        if (handleTouched) {
                            mHandlePaint.setColor(HANDLE_PRESSED_COLOR);
                            mHandlePaint.setStyle(Paint.Style.FILL);
                            invalidate();
                            performClick = false;
                        } else {
                            performClick = true;
                        }
                        break;
                }
                return !performClick;
            }
        };
        this.setOnTouchListener(mKeyboardOntTouchListener);
        this.setPadding(0, (int) convertDpToPixel(TOP_PADDING_DP, context), 0, 0);

        mHandlePaint = new Paint();
        mHandlePaint.setColor(HANDLE_COLOR);
        mHandlePaint.setStyle(Paint.Style.FILL);
        mHandlePaint.setPathEffect(HANDLE_CORNER_EFFECT);

        mHandlePath = new Path();

    }

    public static boolean isAllignBottomCenter() {
        return allignBottomCenter;
    }

    public static void setAlignBottomCenter(boolean allignBottomCenter) {
        MyKeyboardFloating.allignBottomCenter = allignBottomCenter;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isAllignBottomCenter()) {
            RelativeLayout.LayoutParams relativeLayoutParams = (RelativeLayout.LayoutParams) getLayoutParams();

            //relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            //relativeLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

            relativeLayoutParams.addRule(RelativeLayout.BELOW, resids);
            setLayoutParams(relativeLayoutParams);
        }
    }

    @Override
    public void onSizeChanged(int xNew, int yNew, int xOld, int yOld) {
        super.onSizeChanged(xNew, yNew, xOld, yOld);
        width = xNew;
        drawHandle();
    }

    private void drawHandle() {
        mHandlePath.rewind();
        mHandlePath.moveTo(0, topPaddingPx);
        mHandlePath.lineTo(0, topPaddingPx - 25);
        mHandlePath.lineTo(width / 3, topPaddingPx - 25);
        mHandlePath.lineTo(width / 3, 0);
        mHandlePath.lineTo(2 * width / 3, 0);
        mHandlePath.lineTo(2 * width / 3, topPaddingPx - 25);
        mHandlePath.lineTo(width, topPaddingPx - 25);
        mHandlePath.lineTo(width, topPaddingPx);
        // Draw this line twice to fix strange artifact in API21
        mHandlePath.lineTo(width, topPaddingPx);
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = mHandlePaint;
        Path path = mHandlePath;
        canvas.drawPath(path, paint);

    }

    public boolean isVisible() {
        return this.getVisibility() == View.VISIBLE;
    }

    public void show(View v) {
        this.setVisibility(View.VISIBLE);
        this.setEnabled(true);
    }

    public void hide() {
        this.setVisibility(View.GONE);
        this.setEnabled(false);
    }


    public void registerEditText(int resid) {
        resids = resid;
        EditText edittext = ((Activity) getContext()).findViewById(resid);
        edittext.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) show(v);
//                else hide();
            }
        });

        edittext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //show(v);
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

    }


    private static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private Rect keepInScreen(int topMargin, int leftMargin) {
        int top = topMargin;
        int left = leftMargin;
        measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int height = getMeasuredHeight();
        int width = getMeasuredWidth();

        int rightCorrection = ((View) getParent()).getPaddingRight();
        int bottomCorrection = ((View) getParent()).getPaddingBottom();
        int leftCorrection = ((View) getParent()).getPaddingLeft();
        int topCorrection = ((View) getParent()).getPaddingTop();

        Rect rootBounds = new Rect();
        ((View) getParent()).getHitRect(rootBounds);
        rootBounds.set(rootBounds.left + leftCorrection, rootBounds.top + topCorrection, rootBounds.right - rightCorrection, rootBounds.bottom - bottomCorrection);

        if (top <= rootBounds.top)
            top = rootBounds.top;
        else if (top + height > rootBounds.bottom)
            top = rootBounds.bottom - height;

        if (left <= rootBounds.left)
            left = rootBounds.left;
        else if (left + width > rootBounds.right)
            left = rootBounds.right - width;

        return new Rect(left, top, left + width, top + height);
    }
}
