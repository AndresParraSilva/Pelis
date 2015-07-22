package ar.com.matotuonda.pelis;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ANDRES on 18/07/2015.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public List posters= new ArrayList<String>();
    private final String LOG_TAG = ImageAdapter.class.getSimpleName();

    public ImageAdapter(Context c, List items) {
        super();
        mContext = c;
        posters.clear();
        posters.addAll(items);
        //for(String unPoster : items) {
        //    posters.add(unPoster);
        //}
       /* posters.add("/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg");
        posters.add("/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg");
        posters.add("/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg");
        posters.add("/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg");
        */
    }

    public int getCount() {
        return posters.size();
    }

    public Object getItem(int position) {
        return posters.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView= (ImageView) convertView;
        if (imageView == null) {
            imageView = new ImageView(mContext);
            //imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
            //imageView.setScaleType(ImageView.ScaleType. CENTER_CROP);
            //imageView.setPadding(8, 8, 8, 8);
        }
        //String url = "http://image.tmdb.org/t/p/w342//5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg"; //getItem(position);
        final String POSTERS_BASE_URL= "http://image.tmdb.org/t/p/w342/";
        String url= POSTERS_BASE_URL + (String) getItem(position);
        Log.v(LOG_TAG, url);

        Picasso.with(mContext)
                .load(url)
                .placeholder(R.drawable.movie_icon_300)
                .error(R.drawable.movie_icon_300_r)
                .into(imageView);
        return imageView;
    }
/*        // create a new ImageView for each item referenced by the Adapter
    public View getViewSinPicasso(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(300, 300));
            imageView.setScaleType(ImageView.ScaleType. CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.movie_icon_300_r,
            R.drawable.movie_icon_300_r,
            R.drawable.movie_icon_300_g,
            R.drawable.movie_icon_300_r,
            R.drawable.movie_icon_300_b,
            R.drawable.movie_icon_300_r,
            R.drawable.movie_icon_300_g,
            R.drawable.movie_icon_300_r,
            R.drawable.movie_icon_300_b,
            R.drawable.movie_icon_300_r,
            R.drawable.movie_icon_300_g,
            R.drawable.movie_icon_300_r,
            R.drawable.movie_icon_300_b,
            R.drawable.movie_icon_300_r,
            R.drawable.movie_icon_300_g,
            R.drawable.movie_icon_300_r,
            R.drawable.movie_icon_300_b,
            R.drawable.movie_icon_300_r,
            R.drawable.movie_icon_300_g,
            R.drawable.movie_icon_300_r,
            R.drawable.movie_icon_300_b,
            R.drawable.movie_icon_300_r,
            R.drawable.movie_icon_300_g,
            R.drawable.movie_icon_300_r,
            R.drawable.movie_icon_300_b
    };
*/
}