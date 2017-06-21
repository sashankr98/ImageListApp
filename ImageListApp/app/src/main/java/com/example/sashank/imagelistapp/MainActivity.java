package com.example.sashank.imagelistapp;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.jar.Manifest;

public class MainActivity extends AppCompatActivity implements Communicator, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{


    public static final int CHOOSE_PICTURE = 1;

    public static final int OPEN_CAMERA = 2;
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 3;

    ListView listView;
    ImageListAdapter adapter;

    File tempImageFile;
    Uri tempUri;

    String tempCaption = "Caption";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        adapter = new ImageListAdapter(this);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_bar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.addPictureFromGallery:
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select File Browser"),CHOOSE_PICTURE);
                return true;

            case R.id.addPictureFromCamera:
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_WRITE_EXTERNAL_STORAGE);
                }
                else
                    startCameraIntent();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case CHOOSE_PICTURE:
                    showCaptionDialog();
                    adapter.list.add(new Row(data.getData()));
                    adapter.notifyDataSetChanged();
                    break;

                case OPEN_CAMERA:
                    showCaptionDialog();
                    adapter.list.add(new Row(tempUri));
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this,"Image saved at " + tempImageFile.getAbsolutePath(),Toast.LENGTH_SHORT).show();
                    break;

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "Long Press for Edit Options", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showEditDialog(position);
        return true;
    }

    public void startCameraIntent() {
        String fileName = Calendar.getInstance().getTime().toString() + ".jpg";
        tempImageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
        tempUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", tempImageFile);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(intent, OPEN_CAMERA);
    }

    public class Row
    {
        Uri imgUri;
        String caption;

        Row(Uri uri) {
            imgUri = uri;
        }
    }

    private class ViewHolder {
        ImageView imageView;
        TextView caption;

        ViewHolder(View v) {
            imageView = (ImageView) v.findViewById(R.id.imageView);
            caption = (TextView) v.findViewById(R.id.caption);
        }
    }
    private class ImageListAdapter extends BaseAdapter
    {

        ArrayList<Row> list;
        transient Context context;
        ImageListAdapter(Context c) {
            list = new ArrayList<>();
            context = c;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.list_row_item, parent, false);
                holder = new ViewHolder(row);
                row.setTag(holder);
            }
            else
                holder = (ViewHolder) row.getTag();

            Row temp = list.get(position);
            holder.imageView.setImageURI(temp.imgUri);
            holder.caption.setText(temp.caption);
            return row;
        }
    }

    public void showCaptionDialog()
    {
        FragmentManager manager = getFragmentManager();
        CaptionDialog captionDialog = new CaptionDialog();
        captionDialog.setStyle(DialogFragment.STYLE_NORMAL,R.style.CustomDialog);
        captionDialog.show(manager,"CaptionDialog");
    }

    public void showEditDialog(int position)
    {
        FragmentManager manager = getFragmentManager();
        EditDialog editDialog = EditDialog.newInstance(position);
        editDialog.show(manager,"EditDialog");
    }

    @Override
    public void sendCaptionMessage(String caption) {
        tempCaption = caption;
        adapter.list.get(adapter.getCount()-1).caption=tempCaption;
        adapter.notifyDataSetChanged();
    }

    @Override
    public void sendDeleteMessage(int position) {
        adapter.list.remove(position);
        adapter.notifyDataSetChanged();
        Toast.makeText(this,"Item was deleted",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCameraIntent();
                }
                else
                    Toast.makeText(this,"Oops, can't capture photo if permission isn't granted",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter.getCount()!=0) {
            ArrayList<String> imgUris = new ArrayList<>();
            ArrayList<String> imgCaptions = new ArrayList<>();

            for(Row tempRow : adapter.list) {
                imgUris.add(tempRow.imgUri.toString());
                imgCaptions.add((tempRow.caption));
            }

            outState.putStringArrayList("imgUris",imgUris);
            outState.putStringArrayList("imgCaptions",imgCaptions);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState!=null) {
            ArrayList<String> imgUris = savedInstanceState.getStringArrayList("imgUris");
            ArrayList<String> imgCaptions = savedInstanceState.getStringArrayList("imgCaptions");
            if (imgUris!=null && imgCaptions!=null) {
                for (int i = 0; i < imgUris.size(); ++i) {
                    adapter.list.add(new Row(Uri.parse(imgUris.get(i))));
                    adapter.list.get(i).caption = imgCaptions.get(i);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }
}