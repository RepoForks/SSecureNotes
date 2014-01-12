package com.hooloovoo.securenotes.widget;


import java.util.ArrayList;


import com.hooloovoo.securenotes.R;
import com.hooloovoo.securenotes.object.ImageLoader;
import com.hooloovoo.securenotes.object.Note;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NoteAdapter extends BaseAdapter {
	
	private class ViewHolder{
		public TextView nome;
		public TextView desc;
		public TextView data;
		public ImageView image;
	}
	
	public ArrayList<Note> data;
	Context mContext;
	LayoutInflater mLayoutInflater;
	ViewHolder mViewHolder;
	ImageLoader mImgLoader;
	//position for animation
	int mLastPosition = -1;
	
	boolean noImage = false;
	boolean bNoImage = false; //alla view prima suppongo che sia con l'immagine
    boolean onlyImage;
	
	

	public NoteAdapter(Context context, ArrayList<Note> data) {
		this.data = data;
		this.mContext = context;
		this.mLayoutInflater = (LayoutInflater) 
				this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.mImgLoader = new ImageLoader(mContext); 
		
	}

    public NoteAdapter(Context context){
        this.data = new ArrayList<Note>();
        this.mContext = context;
        this.mLayoutInflater = (LayoutInflater)
                this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mImgLoader = new ImageLoader(mContext);
    }

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		mViewHolder = null;
		final Note note = (Note) data.get(position);
		noImage = (note.getImageLen()==1)?true:false;
        onlyImage = note.getmName().equals("") && note.getmDesc().equals("");
		if(!noImage ){
            if(onlyImage){
                view = mLayoutInflater.inflate(R.layout.sv_layout_listview_item_img, null);
            }else{
                view = mLayoutInflater.inflate(R.layout.sv_layout_listview_item_img_text, null);
            }

			mViewHolder = new ViewHolder();
			mViewHolder.image = (ImageView) view.findViewById(R.id.imageView1_listnotes);
			view.setTag(mViewHolder);
			bNoImage = false;
		}else if(noImage  ){
			view = mLayoutInflater.inflate(R.layout.sv_layout_listview_item_text, null);
			mViewHolder = new ViewHolder();
			view.setTag(mViewHolder);
			bNoImage = true;
		}else{
			mViewHolder = (ViewHolder) view.getTag();
		}
        //view.setBackgroundColor(Color.parseColor("#669900"));

        if(!onlyImage){
            //set text
            mViewHolder.nome = (TextView) view.findViewById(R.id.textView_Nome);
            mViewHolder.desc = (TextView) view.findViewById(R.id.textView_Desc);



		    mViewHolder.nome.setText(note.getmName());
            if(note.getmName().equals("") && !note.getmDesc().equals("")){
                mViewHolder.nome.setText(note.getmDesc());
            }else{
		        mViewHolder.desc.setText(note.getmDesc());
            }
        }

		//imposto la foto!
		
		if(!noImage){
            mImgLoader.loadBitmap(note, mViewHolder.image,onlyImage);
			Log.w("CARICA BITMAP", "dentro");
		}
		
		//gestisco l'animazione 
		TranslateAnimation animation = null;
        if (position > mLastPosition) {
            animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);

            animation.setDuration(600);
            view.startAnimation(animation);
            mLastPosition = position;
        }
		return view;
	}
	
	public void add(Object ob){
		data.add((Note) ob);
	}
	
	public void remove(Object ob){
		data.remove(ob);
	}
	
	/**
	 * This method update object in position position with object ob
	 * @param ob
	 * @param position
	 */
	public void update(Object ob, int position){
		Note toUpdate = data.get(position);
		Note forUpdate = (Note) ob;
//		Log.d("IMAGE_ADAPTER", "LENGH FOR UPFATE"+ forUpdate.getmImage().length);
//		Log.d("IMAGE_ADAPTER", "LENGH TO UPFATE"+ toUpdate.getmImage().length);
		if(toUpdate.getImageLen()!= forUpdate.getImageLen()){
			data.remove(position);
			data.add(position,forUpdate);
		}else{
			toUpdate.setmId(forUpdate.getmId());
			toUpdate.setmName(forUpdate.getmName());
			toUpdate.setmDesc(forUpdate.getmDesc());
			toUpdate.setmDate(forUpdate.getmDate());
		}
//		Bitmap img;
//		if(( img = forUpdate.getBitmapImage()) != null) toUpdate.setBitmapImage(img);
	}
	
	
	
	

}
