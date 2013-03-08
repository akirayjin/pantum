package com.pantum;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CreditListAdapter extends BaseAdapter {
	private static final int TYPE_ITEM_ONE = 0;
	private static final int TYPE_ITEM_TWO = 1;
	private static final int TYPE_MAX_COUNT = TYPE_ITEM_TWO + 1;

	public ArrayList<String> nameList;
	public Context context;
	private LayoutInflater mInflater;

	public CreditListAdapter(Context context, ArrayList<String> nameList){
		this.nameList = nameList;
		this.mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return nameList.size();
	}

	@Override
	public Object getItem(int position) {
		return nameList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		return (position%2==0) ? TYPE_ITEM_ONE : TYPE_ITEM_TWO;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		int type = getItemViewType(position);
		if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case TYPE_ITEM_ONE:
                    convertView = mInflater.inflate(R.layout.credit_line_one, null);
                    holder.textView = (TextView)convertView.findViewById(R.id.credit_text);
                    break;
                case TYPE_ITEM_TWO:
                    convertView = mInflater.inflate(R.layout.credit_line_two, null);
                    holder.textView = (TextView)convertView.findViewById(R.id.credit_text);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

		holder.textView.setText(nameList.get(position));
		/*holder.itemImage.setImageResource(imgid[itemDetailsrrayList.get(position).getImageNumber() - 1]);*/

		return convertView;
	}

	static class ViewHolder {
		TextView textView;
		ImageView itemImage;
	}

}
