package io.doist.material.widget.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.doist.material.R;
import io.doist.material.reflection.ReflectionUtils;
import io.doist.material.res.MaterialResources;

public class MaterialWidgetHandler {
    private static final boolean sSkip = Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT;

    private static final Class<?> StyleableClass = ReflectionUtils.getClass("com.android.internal.R$styleable");

    public static final String STYLEABLE_VIEW = "View";
    public static final String STYLEABLE_IMAGE_VIEW = "ImageView";
    public static final String STYLEABLE_TEXT_VIEW = "TextView";

    private static int[] sOriginalViewStyleable;
    private static int[] sHiddenViewStyleable;
    private static int[] sOriginalImageViewStyleable;
    private static int[] sHiddenImageViewStyleable;
    private static int[] sOriginalTextViewStyleable;
    private static int[] sHiddenTextViewStyleable;

    public static AttributeSet hideStyleableAttributes(AttributeSet set, String... styleables) {
        if (sSkip) {
            return set;
        }

        for (String styleable : styleables) {
            switch (styleable) {
                case STYLEABLE_VIEW:
                    if (sOriginalViewStyleable == null) {
                        // Keep original view styleable values.
                        sOriginalViewStyleable =
                                (int[]) ReflectionUtils.getDeclaredFieldValue(StyleableClass, STYLEABLE_VIEW, null);

                        sHiddenViewStyleable = createHiddenStyleable(sOriginalViewStyleable, "View_background");

                    }

                        ReflectionUtils.setDeclaredFieldValue(
                                StyleableClass,
                                STYLEABLE_VIEW,
                                null,
                                sHiddenViewStyleable);
                    break;

                case STYLEABLE_IMAGE_VIEW:
                    if (sOriginalImageViewStyleable == null) {
                        // Keep original image view styleable values.
                        sOriginalImageViewStyleable =
                                (int[]) ReflectionUtils
                                        .getDeclaredFieldValue(StyleableClass, STYLEABLE_IMAGE_VIEW, null);

                        sHiddenImageViewStyleable = createHiddenStyleable(sOriginalImageViewStyleable, "ImageView_src");
                    }

                        ReflectionUtils.setDeclaredFieldValue(
                                StyleableClass,
                                STYLEABLE_IMAGE_VIEW,
                                null,
                                sHiddenImageViewStyleable);
                    break;

                case STYLEABLE_TEXT_VIEW:
                    if (sOriginalTextViewStyleable == null) {
                        // Keep original image view styleable values.
                        sOriginalTextViewStyleable =
                                (int[]) ReflectionUtils
                                        .getDeclaredFieldValue(StyleableClass, STYLEABLE_TEXT_VIEW, null);

                        sHiddenTextViewStyleable = createHiddenStyleable(
                                sOriginalTextViewStyleable,
                                "TextView_drawableLeft",
                                "TextView_drawableTop",
                                "TextView_drawableRight",
                                "TextView_drawableBottom",
                                "TextView_drawableStart",
                                "TextView_drawableEnd");
                    }

                        ReflectionUtils.setDeclaredFieldValue(
                                StyleableClass,
                                STYLEABLE_TEXT_VIEW,
                                null,
                                sHiddenTextViewStyleable);
                    break;
            }
        }
        return set;
    }

    public static void restoreStyleableAttributes(String... styleables) {
        if (sSkip) {
            return;
        }

        for (String styleable : styleables) {
            switch (styleable) {
                case STYLEABLE_VIEW:
                    ReflectionUtils.setDeclaredFieldValue(
                            StyleableClass, STYLEABLE_VIEW, null, sOriginalViewStyleable);
                    break;

                case STYLEABLE_IMAGE_VIEW:
                    ReflectionUtils.setDeclaredFieldValue(
                            StyleableClass, STYLEABLE_IMAGE_VIEW, null, sOriginalImageViewStyleable);
                    break;

                case STYLEABLE_TEXT_VIEW:
                    ReflectionUtils.setDeclaredFieldValue(
                            StyleableClass, STYLEABLE_TEXT_VIEW, null, sOriginalTextViewStyleable);
                    break;
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    public static void init(View view, AttributeSet set, int defStyle, String[] styleables) {
        if (sSkip) {
            return;
        }

        Context context = view.getContext();
        MaterialResources resources = MaterialResources.getInstance(context, context.getResources());
        for (String styleable : styleables) {
            switch (styleable) {
                case STYLEABLE_VIEW:
                    initViewAttributes(context, resources, view, set, defStyle);
                    break;

                case STYLEABLE_IMAGE_VIEW:
                    initImageViewAttributes(context, resources, (ImageView) view, set, defStyle);
                    break;

                case STYLEABLE_TEXT_VIEW:
                    initTextViewAttributes(context, resources, (TextView) view, set, defStyle);
                    break;
            }
        }
    }

    private static void initViewAttributes(Context context, MaterialResources resources, View view,
                                           AttributeSet set, int defStyle) {
        TypedArray ta = context.obtainStyledAttributes(set, R.styleable.MaterialView, defStyle, 0);
        try {
            if (ta.hasValue(R.styleable.MaterialImageView_android_src)) {
                Drawable drawable =
                        resources.getDrawable(ta.getResourceId(R.styleable.MaterialImageView_android_src, 0));

                final int paddingLeft = view.getPaddingLeft();
                final int paddingTop = view.getPaddingTop();
                final int paddingRight = view.getPaddingRight();
                final int paddingBottom = view.getPaddingBottom();

                // Init background.
                view.setBackground(drawable);

                // Maintain horizontal and vertical padding.
                if (paddingLeft > 0 && paddingRight > 0) {
                    view.setPadding(
                            paddingLeft,
                            view.getPaddingTop(),
                            paddingRight,
                            view.getPaddingBottom());
                }

                if (paddingTop > 0 && paddingBottom > 0) {
                    view.setPadding(
                            view.getPaddingLeft(),
                            paddingTop,
                            view.getPaddingRight(),
                            paddingBottom);
                }
            }
        } finally {
            ta.recycle();
        }
    }

    private static void initImageViewAttributes(Context context, MaterialResources resources, ImageView imageView,
                                                AttributeSet set, int defStyle) {
        TypedArray ta = context.obtainStyledAttributes(set, R.styleable.MaterialImageView, defStyle, 0);
        try {
            if (ta.hasValue(R.styleable.MaterialImageView_android_src)) {
                Drawable drawable =
                        resources.getDrawable(ta.getResourceId(R.styleable.MaterialImageView_android_src, 0));
                // Init image drawable.
                imageView.setImageDrawable(drawable);
            }
        } finally {
            ta.recycle();
        }
    }

    private static void initTextViewAttributes(Context context, MaterialResources resources, TextView textView,
                                               AttributeSet set, int defStyle) {
        Drawable drawableLeft, drawableTop, drawableRight, drawableBottom, drawableStart, drawableEnd;
        drawableLeft = drawableTop = drawableRight = drawableBottom = drawableStart = drawableEnd = null;

        TypedArray ta = context.obtainStyledAttributes(set, R.styleable.MaterialTextView, defStyle, 0);
        try {
            int N = ta.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = ta.getIndex(i);
                if (attr == R.styleable.MaterialTextView_android_drawableLeft) {
                    drawableLeft = resources.getDrawable(ta.getResourceId(attr, 0));
                } else if (attr == R.styleable.MaterialTextView_android_drawableTop) {
                    drawableTop = resources.getDrawable(ta.getResourceId(attr, 0));
                } else if (attr == R.styleable.MaterialTextView_android_drawableRight) {
                    drawableRight = resources.getDrawable(ta.getResourceId(attr, 0));
                } else if (attr == R.styleable.MaterialTextView_android_drawableBottom) {
                    drawableBottom = resources.getDrawable(ta.getResourceId(attr, 0));
                } else if (attr == R.styleable.MaterialTextView_android_drawableStart) {
                    drawableStart = resources.getDrawable(ta.getResourceId(attr, 0));
                } else if (attr == R.styleable.MaterialTextView_android_drawableEnd) {
                    drawableEnd = resources.getDrawable(ta.getResourceId(attr, 0));
                }
            }
        } finally {
            ta.recycle();
        }

        if (drawableLeft != null || drawableTop != null || drawableRight != null || drawableBottom != null) {
            textView.setCompoundDrawablesWithIntrinsicBounds(drawableLeft, drawableTop, drawableRight, drawableBottom);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (drawableStart != null || drawableEnd != null) {
                Drawable[] drawablesRelative = textView.getCompoundDrawablesRelative();
                if (drawableStart == null) {
                    drawableStart = drawablesRelative[0];
                }
                if (drawableEnd == null) {
                    drawableEnd = drawablesRelative[2];
                }
                textView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableStart, drawablesRelative[1],
                                                                         drawableEnd, drawablesRelative[2]);
            }
        }
    }

    private static int[] createHiddenStyleable(int[] styleable, String... hiddenValues) {
        int[] newStyleable = new int[styleable.length];
        System.arraycopy(styleable, 0, newStyleable, 0, styleable.length);
        for (String hiddenValue : hiddenValues) {
            int hiddenIndex = (int) ReflectionUtils.getDeclaredFieldValue(StyleableClass, hiddenValue, null);
            newStyleable[hiddenIndex] = 0;
        }
        return newStyleable;
    }
}
