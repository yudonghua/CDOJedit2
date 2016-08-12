package com.example.pc.cdojedit2;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by PC on 2016/8/12.
 */
public class AutoCompleteEditText extends EditText{
    public String str0[]={"int","double","void","char","float","double","short","long","signed","unsigned","struct","union","enum","typedef","sizeof","auto","static","register","extern","const",
            "volatile","return","continue","break","goto","if","else","switch","case","default","for","do","while","String","string","include","public","private","protected"};
    String strDef;
    SpannableString spanText[] = new SpannableString[str0.length];
    public int startPositon,start;
    int endPositon;
    int savetime=0;
    String savestring[]=new String[1000];
    int index[]=new int[1000];
    boolean aBoolean,addBoolean=false,changing=false;
    public String string,string2;
    public void save(String string,int index0){
        savetime=0;
        for(int i=1;i<501;i++){
            savestring[i-1]=savestring[i];
            index[i-1]=index[i];
        }
        savestring[500]=AutoCompleteEditText.this.getText().toString();
        index[500]=index0;

    }
    public void up(){
        // Log.d("???",AutoCompleteEditText.this.getText().toString()+"::");
        if(AutoCompleteEditText.this.getText().toString().equals(""))return;
        savetime++;
        savestring[501]=AutoCompleteEditText.this.getText().toString();
        index[501]=AutoCompleteEditText.this.getSelectionStart();
        AutoCompleteEditText.this.setText(savestring[500]);
        AutoCompleteEditText.this.setSelection(index[500]);
        for(int i=999;i>1;i--){
            savestring[i]=savestring[i-1];
            index[i]=index[i-1];
        }
        Log.d("savestring",AutoCompleteEditText.this.getText().toString());
    }
    public void down(){
        Log.d("savestring",AutoCompleteEditText.this.getText().toString());
        if(savetime==0)return;
        savetime--;
        AutoCompleteEditText.this.setText(savestring[502]);
        AutoCompleteEditText.this.setSelection(index[502]);
        for(int i=0;i<999;i++){
            savestring[i]=savestring[i+1];
            index[i]=index[i+1];
        }
    }

    public AutoCompleteEditText(Context context) {
        this(context, null);
        for(int i=0;i<str0.length;i++){
            spanText[i] = new SpannableString(str0[i]);
            spanText[i].setSpan(new ForegroundColorSpan(Color.BLUE),0,spanText[i].length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }
    public AutoCompleteEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoCompleteEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.addTextChangedListener(new TextWatcher() {

            String strNow = null;
            String strOld = null;
            int strLength = -1;
            public void changecolor(CharSequence s,int index){
                SpannableStringBuilder ssb = new SpannableStringBuilder(AutoCompleteEditText.this.getText().toString());
                for(int i=0;i<str0.length;i++){
                    Pattern pattern = Pattern.compile("^"+str0[i]);
                    Matcher matcher = pattern.matcher(AutoCompleteEditText.this.getText().toString());
                    while (matcher.find()) {
                        String group = matcher.group();
                        ClickableSpan cs = new MyClickableSpan(group);
                        ssb.setSpan(cs, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                for(int i=0;i<str0.length;i++){
                    Pattern pattern = Pattern.compile("[^a-zA-Z]"+str0[i]+"[^a-zA-Z]");
                    Matcher matcher = pattern.matcher(AutoCompleteEditText.this.getText().toString());
                    while (matcher.find()) {
                        String group = matcher.group();
                        ClickableSpan cs = new MyClickableSpan(group);
                        ssb.setSpan(cs, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
                AutoCompleteEditText.this.setText(ssb);
                AutoCompleteEditText.this.setSelection(index);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                return;
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(changing)return;
                if(addBoolean)return;
                //        Log.d(startPositon +"::",""+editText.getSelectionStart());
                aBoolean = startPositon >= AutoCompleteEditText.this.getSelectionStart();
                if (aBoolean) {
                    startPositon=AutoCompleteEditText.this.getSelectionStart();
                    string = s.toString().substring(0, AutoCompleteEditText.this.getSelectionStart());
                    strNow = null;
                    strOld = null;
                    return;
                }
                if (s.toString().charAt(AutoCompleteEditText.this.getSelectionStart() - 1) < 'A'
                        || s.toString().charAt(AutoCompleteEditText.this.getSelectionStart() - 1) > 'Z'
                        && s.toString().charAt(AutoCompleteEditText.this.getSelectionStart() - 1) < 'a'
                        || s.toString().charAt(AutoCompleteEditText.this.getSelectionStart() - 1) > 'z') {
                    save(s.toString(),AutoCompleteEditText.this.getSelectionStart()-1);
                    if(s.toString().charAt(AutoCompleteEditText.this.getSelectionStart() - 1)!='{'
                            &&s.toString().charAt(AutoCompleteEditText.this.getSelectionStart() - 1)!='\n')
                        changecolor(s,AutoCompleteEditText.this.getSelectionStart());
                    strNow = null;
                    string = s.toString().substring(0, AutoCompleteEditText.this.getSelectionStart());
                } else if (strNow == null)
                    strNow = s.toString().charAt(AutoCompleteEditText.this.getSelectionStart() - 1) + "";
                else strNow += s.toString().charAt(AutoCompleteEditText.this.getSelectionStart() - 1);
                return;
            }


            public void afterTextChanged(Editable s) {
                if(changing)return;
                if (aBoolean){aBoolean=false;return;}
                if (s.toString().charAt(AutoCompleteEditText.this.getSelectionStart() - 1)=='{') {
                    changing=true;
                    int num=0;
                    for(int i=0;i<AutoCompleteEditText.this.getSelectionStart();i++){
                        if(AutoCompleteEditText.this.getText().toString().charAt(i) == '{')num++;
                        if(AutoCompleteEditText.this.getText().toString().charAt(i) == '}')num--;
                    }
                    String tab="";
                    for(int i=0;i<num-1;i++)tab+="\u3000";
                    s.insert(AutoCompleteEditText.this.getSelectionStart(), "\n"+tab+"\u3000"+"\n"+tab+"}");
                    AutoCompleteEditText.this.setSelection(AutoCompleteEditText.this.getSelectionStart()-num-1);
                    changing=false;
                    return;
                }
                if (s.toString().charAt(AutoCompleteEditText.this.getSelectionStart() - 1)=='\n') {
                    changing=true;
                    int num=0;
                    for(int i=0;i<AutoCompleteEditText.this.getSelectionStart();i++){
                        if(AutoCompleteEditText.this.getText().toString().charAt(i) == '{')num++;
                        if(AutoCompleteEditText.this.getText().toString().charAt(i) == '}')num--;
                    }
                    String tab="";
                    for(int i=0;i<num;i++)tab+="\u3000";
                    s.insert(AutoCompleteEditText.this.getSelectionStart(),tab);
                    AutoCompleteEditText.this.setSelection(AutoCompleteEditText.this.getSelectionStart());
                    changing=false;
                    return;
                }
                int i;
                if(addBoolean){
                    AutoCompleteEditText.this.setSelection(start,endPositon);

                    addBoolean=false;
                    return;
                }
                startPositon = AutoCompleteEditText.this.getSelectionStart();
                for (i = 0; i < str0.length; i++) {
                    strDef = str0[i];
                    if(strNow!=null){
                        if (strDef.startsWith(strNow) && !strDef.equals(strNow)) {
                            if(strNow.length()>1){
                                start++;
                                addBoolean=true;
                                if(string!=null){
                                    endPositon = strDef.length()+AutoCompleteEditText.this.getSelectionStart()-strNow.length();
                                    s.insert(AutoCompleteEditText.this.getSelectionStart(),strDef.substring(strNow.length()));
                                }
                                else {
                                    endPositon=strDef.length();
                                    s.insert(strNow.length(),strDef.substring(strNow.length()));
                                }

                                return;
                            }

                            if(string!=null)
                                endPositon = strDef.length()+AutoCompleteEditText.this.getSelectionStart()-1;
                            else endPositon=strDef.length();
                            if(string!=null){
                                addBoolean=true;
                                start=AutoCompleteEditText.this.getSelectionStart()+strNow.length()-1;
                                Log.d("strDef",strDef.length()+"");
                                endPositon=AutoCompleteEditText.this.getSelectionStart()+strDef.length()-1;
                                s.insert(AutoCompleteEditText.this.getSelectionStart(),strDef.substring(1));
                            }
                            else {
                                addBoolean=true;
                                start=strNow.length();
                                endPositon=strDef.length();
                                s.insert(1,strDef.substring(1));
                            }
                            break;
                        }
                    }
                }
                if(i==str0.length){strNow=null;string = s.toString().substring(0, AutoCompleteEditText.this.getSelectionStart());}
                return;
            }

        });
    }

    public void changecolor(CharSequence s,int index){
        SpannableStringBuilder ssb = new SpannableStringBuilder(AutoCompleteEditText.this.getText().toString());
        for(int i=0;i<str0.length;i++){
            Pattern pattern = Pattern.compile("^"+str0[i]);
            Matcher matcher = pattern.matcher(AutoCompleteEditText.this.getText().toString());
            while (matcher.find()) {
                String group = matcher.group();
                ClickableSpan cs = new MyClickableSpan(group);
                ssb.setSpan(cs, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        for(int i=0;i<str0.length;i++){
            Pattern pattern = Pattern.compile("[^a-zA-Z]"+str0[i]+"[^a-zA-Z]");
            Matcher matcher = pattern.matcher(AutoCompleteEditText.this.getText().toString());
            while (matcher.find()) {
                String group = matcher.group();
                ClickableSpan cs = new MyClickableSpan(group);
                ssb.setSpan(cs, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        AutoCompleteEditText.this.setText(ssb);
        AutoCompleteEditText.this.setSelection(index);
    }
    class MyClickableSpan extends ClickableSpan {
        private String group;

        public MyClickableSpan() {
        }

        public MyClickableSpan(String group) {
            this.group = group;
        }
        public void updateDrawState(TextPaint ds) {
            // TODO Auto-generated method stub
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }

        @Override
        public void onClick(View widget) {
        }
    }
}
