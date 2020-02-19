
package com.example.displayingbitamapdemov2;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.displayingbitamapdemov2.image.ImageCache;
import com.example.displayingbitamapdemov2.image.ImageFetcher;
import com.example.displayingbitamapdemov2.image.ImageWorker;
import com.example.displayingbitamapdemov2.image.RecyclingImageView;

public class ImageGridFragment extends Fragment {
    private static final String TAG = "ImageGridFragment";
    private ImageWorker mImageWorker;
    private ImageAdapter mAdapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	mImageWorker = new ImageFetcher(getActivity(), 50);
    	
    	ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams();
    	
    	//内存缓存大小为应用可用内存的1/4
    	cacheParams.setMemCacheSizePercent(0.25f);
    	
    	mImageWorker.initImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater,
    		@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    	
    	View v = inflater.inflate(R.layout.list, container, false);
    	ListView listView = (ListView) v.findViewById(R.id.image_list);
    	mAdapter = new ImageAdapter(getActivity());
    	listView.setAdapter(mAdapter);
    	
    	listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            	
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
                    	mImageWorker.setPauseWork(true);
                    }
                } else {
                	mImageWorker.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
            }
        });
    	
    	return v;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        mImageWorker.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageWorker.setPauseWork(false);
        mImageWorker.setExitTasksEarly(true);
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }
    
    
    private class ImageAdapter extends BaseAdapter {
    	private final Context mContext;
    	private LayoutInflater mInflater;
    	
    	public ImageAdapter(Context context) {
    		this.mContext = context;
    		this.mInflater = LayoutInflater.from(mContext);
    	}
    	
		@Override
		public int getCount() {
			return Images.imageThumbUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item, parent, false);
				imageView = (RecyclingImageView) convertView.findViewById(R.id.img);
			} else {
				imageView = (RecyclingImageView) convertView.findViewById(R.id.img);
			}
			
//			imageView = (RecyclingImageView) convertView.findViewById(R.id.img);
			//参数：url,imageview
			mImageWorker.loadImage(Images.imageThumbUrls[position], imageView);
			
			return convertView;
		}
    }
}