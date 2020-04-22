package kostal.com.widgetslibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

public class BankEditText extends EditText {

    private  boolean isCardNumber  = false;
    private  int splitNumber       = 4;  //分割数
    private Drawable mClearDrawable;


    public static final int MAX_CONTENT_LENGHT = 21;



    public BankEditText(Context context) {
        super(context);
    }

    public BankEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BankEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
       // 设置单行显示所有输入框内容
        setSingleLine();
        // 设置输入框可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);


        TypedArray t = this.getResources().obtainAttributes(attrs, R.styleable.BanckEditText);
        isCardNumber =  t.getBoolean(R.styleable.BanckEditText_isCardNumber, isCardNumber);
        splitNumber = t.getInt(R.styleable.BanckEditText_splitNumber,splitNumber);

        t.recycle();

        if(isCardNumber)
        {
            setInputType(InputType.TYPE_CLASS_NUMBER);

        }

        iniEvent();
    }

    private boolean isTextChanged = false;

    private String content;
    // 卡号最大长度,卡号一般最长21位
  //  public static final int MAX_CONTENT_LENGHT = 21;
    // 缓冲分隔后的新内容串
    private String result = "";

    private void iniEvent()
    {
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            //before 输入的内容之前长度
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handleInputContent(s,before);


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //每隔splitNumber 个数字增加一个空格
    private void handleInputContent(CharSequence s, int before) {

        content = s.toString();
        if (isTextChanged) {
            isTextChanged = false;
            return;
        }
        isTextChanged = true;



        content = s.toString();
//先缓存输入框内容
        result = content;
//去掉空格，以防止用户自己输入空格
        content = content.replace(" ", "");
// 限制输入的数字位数最多21位（银行卡号一般最多21位）
        if (content != null && content.length() <= MAX_CONTENT_LENGHT) {
            result = "";
            int i = 0;
// 先把splitNumber倍的字符串进行分隔
            while (i + splitNumber < content.length()) {
                result += content.substring(i, i + splitNumber) + " ";
                i += splitNumber;
            }
// 最后把不够splitNumber倍的字符串加到末尾
            result += content.substring(i, content.length());
        } else {
//如果用户输入的位数大于21位
            result = result.substring(0, result.length() - 1);
        }

// 获取光标开始位置
// 必须放在设置内容之前
        int j = getSelectionStart();
        setText(result);
// 处理光标位置
        handleCursor(before, j);




    }

    private void handleCursor(int before, int j) {
// 处理光标位置
        try {
            if (j + 1 < result.length()) { //字符串中间添加字符
               // 添加字符
                if (before == 0) {
                   //遇到空格，光标跳过空格，定位到空格后的位置
                    if (j % splitNumber + 1 == 0) {
                        setSelection(j + 1);
                    } else {
                        //否则，光标定位到内容之后 （光标默认定位方式）
                        setSelection(result.length());
                    }
               // 回退清除一个字符
                } else if (before == 1) {
                   //回退到上一个位置（遇空格跳过）
                    setSelection(j);
                }
            } else {
                this.setSelection(result.length());
            }
        } catch (Exception e) {
        }
    }


}



    /*onTextChanged(CharSequence text,int start,int lengthBefore,int lengthAfter);//参数名也可能是其他命名。

含义：

        0、这个方法表示的是在EditText的内容改变后的回调，即输入或者删除操作完成后的回调。

        1、参数text:表示当前显示的EditText内容，即编辑完成后的内容。

        2、参数start、lengthBefore、lengthAfter：这三个参数可以看做是一组的，它们表示的都是和输入或删除的那一段内容相关的，都是针对改变的内容的参数。

        start:表示数据改变后，添加或删除的内容，在整个数据中的位置下标。
        lengthBefore:表示改变的内容，改变前的长度（只针对删掉的或输入上的内容）。
        lengthAfter:表示改变的内容，改变后的长度（只针对删掉的或输入上的内容）。
        示例一：

        EditText中的原内容是“123”，现在在“2”的后面输入“4”。

        那么在输入完“4”之后，回调方法时，各个参数的值是：

        text="1243";//改变后的内容。

        start=2;//输入的"4"在整个字符串中的下标是2。

        lengthBefore=0;//输入的内容之前长度为0。

        lengthAfter=1;//输入的内容输入后长度是1。

        #这两个表示的是“4”在输入前和输入后的长度。如果是在“2”的后面直接粘贴上“45”两个数，那么这四个参数是：

        text="12453"；start=2;lengthBefore=0;lengthAfter=2;

        示例二：

        EditText中的原内容是“12345”，现在把光标移到“4”后面，delete删除“4”。

        那么在删除掉“4”之后，回调方法时，各个参数的值是：

        text="1235";//改变后的内容。

        start=3;//被删除的"4"在整个字符串中的下标是3。

        lengthBefore=1;//删除的内容之前长度为1。

        lengthAfter=0;//删除的内容输入后长度是0。

        #这两个表示的是“4”在删除前和删除后的长度。如果是多选一次删除“45”两个数，那么这四个参数是：

        text="123"；start=3;lengthBefore=2;lengthAfter=0;
        */