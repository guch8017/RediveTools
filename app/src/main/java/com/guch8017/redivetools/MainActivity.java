package com.guch8017.redivetools;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    boolean hasRootPermission;
    private final static String pcrPackageName = "tw.sonet.princessconnect";
    private Query query;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Test",getFilesDir().getAbsolutePath());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        query = new Query(this);
        hasRootPermission = Utils.rootCheck(getPackageCodePath());

        Toast.makeText(this,"Root Check: " + hasRootPermission, Toast.LENGTH_SHORT).show();
        Log.i("Main","Started");

        // 备份当前账号
        Button btn_backup = findViewById(R.id.btn_backup);
        btn_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAccountData();
            }
        });

        // 清除当前账号
        Button btn_clear = findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeAccountData();
            }
        });

        // 列表显示
        ListView mListView = findViewById(R.id.account_list);
        adapter = new Adapter(this, query.GetLogs());
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int rowID = Integer.parseInt(((TextView)view.findViewById(R.id.row_id)).getText().toString());
                showAccountItemMenu(rowID);
            }
        });
    }

    private void refreshList(){
        adapter.updateData(query.GetLogs());
        adapter.notifyDataSetChanged();
    }

    private void showAccountItemMenu(int id){
        final DBAccountData data = query.GetLog(id);
        if(data == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.err_sql)+"rowID:" + id)
                    .setPositiveButton(getString(R.string.confirm),null);
            builder.create().show();
            return;
        }
        View view = LayoutInflater.from(this).inflate(R.layout.account_dialog, null);
        TextView desc = view.findViewById(R.id.desc);
        TextView m3 = view.findViewById(R.id.M3);
        TextView mh = view.findViewById(R.id.MH);
        TextView nn = view.findViewById(R.id.Nn);
        TextView server = view.findViewById(R.id.server);
        desc.setText(data.description);
        m3.setText(data.M3);
        mh.setText(data.MH);
        nn.setText(data.Nn);
        server.setText(getResources().getStringArray(R.array.server_array)[data.server - 1]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(getString(R.string.saved_data))
                .setView(view)
                .setPositiveButton(getString(R.string.confirm),null)
                .setNegativeButton(getString(R.string.restore), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        restoreAccountData(data);
                    }
                })
                .setNeutralButton(getString(R.string.delete_log), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccountData(data.rowID);
                    }
                });
        builder.create().show();
    }

    private void initializeAccountData(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.initialize))
                .setMessage(getString(R.string.initialize_dialog))
                .setPositiveButton(getString(R.string.cancel),null)
                .setNegativeButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            AccountOperater.accountInitial(MainActivity.this);
                        }catch (Exception e){
                            e.printStackTrace();
                            alertError(e.getMessage());
                        }
                    }
                });
        builder.create().show();
    }

    private void deleteAccountData(final int id){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.delete_log))
                .setMessage(getString(R.string.delete_log_dialog))
                .setPositiveButton(getString(R.string.cancel),null)
                .setNegativeButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        query.DeleteLog(id);
                        refreshList();
                    }
                });
        builder.create().show();
    }

    private void launchGameDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.success))
                .setMessage(getString(R.string.success_dialog))
                .setPositiveButton(getString(R.string.launch), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PackageManager packageManager = MainActivity.this.getPackageManager();
                        Intent intent = packageManager.getLaunchIntentForPackage(pcrPackageName);
                        if(intent != null){
                            startActivity(intent);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null);
        builder.create().show();
    }

    private void restoreAccountData(final DBAccountData data){
        if(!hasRootPermission){
            alertRootPermission();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.restore))
                .setMessage(getString(R.string.restore_dialog))
                .setPositiveButton(getString(R.string.cancel),null)
                .setNegativeButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            AccountOperater.accountRestorer(MainActivity.this,data);
                        }catch (Exception e){
                            e.printStackTrace();
                            alertError(e.getMessage());
                        }
                    }
                });
        builder.create().show();
    }

    private void alertRootPermission(){
        Toast.makeText(this, getString(R.string.err_root_permission),Toast.LENGTH_LONG).show();
    }

    private void alertError(String errorInfo){
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(getString(R.string.error))
                .setMessage(errorInfo)
                .setPositiveButton(getString(R.string.confirm), null);
        builder.create().show();
    }

    private void saveAccountData(){
        if(!hasRootPermission){
            alertRootPermission();
            return;
        }
        final View view = LayoutInflater.from(this).inflate(R.layout.account_info_input, null);
        final EditText text = view.findViewById(R.id.description);
        final Spinner spinner = view.findViewById(R.id.server_spinner);
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.insert))
                .setView(view)
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String desc = text.getText().toString();
                        int server = spinner.getSelectedItemPosition() + 1;
                        saveAccountData(desc,server);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null);
        builder.create().show();
    }

    private void saveAccountData(String description, int server){
        try {
            DBAccountData data = AccountOperater.accountReader(this,server);
            data.description = description;
            query.InsertLog(data);
            Toast.makeText(this,getString(R.string.success) + "\nMH: "+data.MH, Toast.LENGTH_LONG)
            .show();
        }catch (Exception e){
            Log.e("Main","Error Parsing XML");
            e.printStackTrace();
            alertError(e.getMessage());
        }
        refreshList();
    }

    private class Adapter extends BaseAdapter{
        private Context mContext;
        List<DBAccountData> data;

        public Adapter(Context mContext, List<DBAccountData> data){
            super();
            this.mContext = mContext;
            this.data = data;
        }

        public void updateData(List<DBAccountData> data){
            this.data = data;
        }

        @Override
        public int getCount() {    //要绑定的数量
            return data.size();
        }

        @Override
        public Object getItem(int position) {  //根据索引获得该位置的对象
            return null;
        }

        @Override
        public long getItemId(int position) {  //获取条目的id
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            View view;
            viewHolder holder;

            if(convertView == null){
                view = LayoutInflater.from(mContext).inflate(R.layout.account_list_item,parent,false);
                holder = new viewHolder();
                holder.description = view.findViewById(R.id.description);
                holder.rowID = view.findViewById(R.id.row_id);
                view.setTag(holder);
            }else{
                view = convertView;
                holder = (viewHolder) view.getTag();
            }
            holder.description.setText(data.get(position).description);
            holder.rowID.setText(String.valueOf(data.get(position).rowID));
            return view;
        }

        private class viewHolder{
            private TextView description;
            private TextView rowID;
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
