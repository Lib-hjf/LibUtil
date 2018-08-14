package org.hjf.log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.hjf.util.DateUtils;
import org.hjf.util.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Database ui.
 */
public final class DBLogUIActivity extends AppCompatActivity {

    private static final String SHOW_ALL_TAG = "ALL";

    private SpAdapter spAdapter = new SpAdapter(this);
    private LvAdapter lvAdapter = new LvAdapter(this);
    private List<String> selectedTagId = new ArrayList<>();
    private SparseArray<TextView> tagViewCache = new SparseArray<>();
    private LinearLayout llTags;

    public static void start(Context context) {
        Intent intent = new Intent(context, DBLogUIActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_log_db_check);
    }

    @Override
    public void onContentChanged() {
        // spinner
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(spAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tagStr = spAdapter.datas.get(position);
                // show all tag
                if (SHOW_ALL_TAG.equals(tagStr)) {
                    selectedTagId.clear();
                    llTags.removeAllViews();
                }
                // not all tag
                else {
                    int tagId = LogUtil.dbLogger.dblogHelper.findTagIdByTagStr(tagStr);
                    if (selectedTagId.contains(tagId + "")) {
                        return;
                    }
                    selectedTagId.add(tagId + "");
                    TextView tagView = getTagView(tagId, tagStr);
                    llTags.addView(tagView);
                }
                qryDataInDB();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        // list view
        ListView lvContent = findViewById(R.id.lv_content);
        lvContent.setAdapter(lvAdapter);
        // selected tags
        llTags = findViewById(R.id.ll_tags);

        qryAllTag();
        qryDataInDB();
    }

    private void qryAllTag() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> tagStrList = LogUtil.dbLogger.dblogHelper.queryAllTagStrinDB();
                tagStrList.add(0, SHOW_ALL_TAG);
                spAdapter.setDatas(tagStrList);
            }
        }).start();
    }

    private void qryDataInDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 查询
                List<LogEntity> logEntities = LogUtil.dbLogger.dblogHelper.queryLogEntityInDB(DBLogUIActivity.this.selectedTagId);
                lvAdapter.setDatas(logEntities);

            }
        }).start();
    }

    public TextView getTagView(int tagId, String tagStr) {
        TextView textView = tagViewCache.get(tagId);
        if (textView == null) {
            textView = new TextView(this);
            textView.setTag(tagId);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            textView.setBackgroundResource(android.R.color.white);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(10, 5, 10, 5);
            // layout
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_VERTICAL;
            params.leftMargin = 20;
            params.rightMargin = 20;
            textView.setLayoutParams(params);
            // text
            textView.setText(tagStr);
            // click
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int tagId = (int) v.getTag();
                    TextView view = tagViewCache.get(tagId);
                    if (view != null) {
                        selectedTagId.remove(tagId + "");
                        llTags.removeView(view);
                        qryDataInDB();
                    }
                }
            });
            // put view cache list
            tagViewCache.put(tagId, textView);
        }
        return textView;
    }

    private static class SpAdapter extends BaseAdapter {

        private Context context;
        private List<String> datas = new ArrayList<>();

        SpAdapter(Context context) {
            this.context = context;
        }

        void setDatas(@NonNull List<String> datas) {
            SpAdapter.this.datas.clear();
            SpAdapter.this.datas.addAll(datas);
            ((Activity) SpAdapter.this.context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SpAdapter.this.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getCount() {
            return SpAdapter.this.datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(SpAdapter.this.context);
                ((TextView) convertView).setTextColor(SpAdapter.this.context.getResources().getColor(android.R.color.white));
                ((TextView) convertView).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                ((TextView) convertView).setGravity(Gravity.CENTER);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
                convertView.setLayoutParams(params);
            }
            ((TextView) convertView).setText(SpAdapter.this.datas.get(position));
            return convertView;
        }
    }


    private static class LvAdapter extends BaseAdapter {

        private Context context;
        private List<LogEntity> datas = new ArrayList<>();
        private StringBuilder stringBuilder = new StringBuilder();

        LvAdapter(Context context) {
            this.context = context;
        }

        void setDatas(@NonNull List<LogEntity> datas) {
            LvAdapter.this.datas.clear();
            LvAdapter.this.datas.addAll(datas);
            ((Activity) this.context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LvAdapter.this.notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getCount() {
            return LvAdapter.this.datas.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(LvAdapter.this.context)
                        .inflate(R.layout.item_log_db_ui, parent, false);
            }

            LogEntity entity = LvAdapter.this.datas.get(position);

            TextView tvTime = convertView.findViewById(R.id.tv_log_time);
            tvTime.setText(DateUtils.getDate_HMS(LvAdapter.this.context, entity.getTimeStamp()));

            TextView tvTag = convertView.findViewById(R.id.tv_log_tag);
            tvTag.setText(entity.getTag());

            TextView tvContent = convertView.findViewById(R.id.tv_log_content);
            stringBuilder.setLength(0);
            stringBuilder.append(entity.isMainThread() ? "MainThread \n" : "")
                    .append(entity.getClassPath()).append("#").append(entity.getMethodName())
                    .append("\n")
                    .append(entity.getContent());
            tvContent.setText(stringBuilder.toString());
            tvContent.setTextColor(LvAdapter.this.context.getResources().getColor(getColorRes(entity.getLogLevel())));
            return convertView;
        }
    }

    @ColorRes
    private static int getColorRes(int logLevel) {
        switch (logLevel) {
            default:
            case Log.VERBOSE:
                return R.color.log_level_verbose;
            case Log.DEBUG:
                return R.color.log_level_debug;
            case Log.INFO:
                return R.color.log_level_info;
            case Log.WARN:
                return R.color.log_level_warn;
            case Log.ERROR:
                return R.color.log_level_error;
        }
    }
}
