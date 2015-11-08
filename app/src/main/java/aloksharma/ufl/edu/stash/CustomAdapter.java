package aloksharma.ufl.edu.stash;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter
        .ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = -1;

    private String mNavTitles[];
    private int mIcons[];

    private String name;
    private String email;
    private Bitmap bitmap;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        int Holderid;

        TextView RowTextView;
        ImageView RowImageView;
        ImageView profile;

        TextView NameTextView;
        TextView EmailTextView;


        public ViewHolder(View itemView, int ViewType) {
            super(itemView);


            if (ViewType == TYPE_ITEM) {
                RowTextView = (TextView) itemView.findViewById(R.id.rowText);
                RowImageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                Holderid = 1;
            } else {
                NameTextView = (TextView) itemView.findViewById(R.id.name);
                EmailTextView = (TextView) itemView.findViewById(R.id.email);
                profile = (ImageView) itemView.findViewById(R.id.profile_image)
                ;
                if (profile==null)
                    Log.d("Stash: CustomAdapter :", "Cannot find placeholder for profile_image");
                Holderid = 0;
            }
        }

    }

    CustomAdapter(String Titles[], int Icons[], String Name, String Email,
                  Bitmap mBitmap) {
        mNavTitles = Titles;
        mIcons = Icons;
        name = Name;
        email = Email;
        bitmap = mBitmap;
    }

    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R
                    .layout.item_row, parent, false);

            ViewHolder vhItem = new ViewHolder(v, viewType);

            return vhItem;

        } else if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R
                    .layout.header, parent, false);

            ViewHolder vhHeader = new ViewHolder(v, viewType);

            return vhHeader;

        } else if (viewType == TYPE_FOOTER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R
                    .layout.footer, parent, false);

            ViewHolder vhFooter = new ViewHolder(v, viewType);

            return vhFooter;
        }
        return null;

    }

    @Override
    public void onBindViewHolder(CustomAdapter.ViewHolder holder, int
            position) {
        if (holder.Holderid == 1) {
            holder.RowTextView.setText(mNavTitles[position - 1]);
            holder.RowImageView.setImageResource(mIcons[position - 1]);
        } else {
            if (bitmap != null)
                holder.profile.setImageBitmap(bitmap);
            holder.NameTextView.setText(name);
            holder.EmailTextView.setText(email);
        }
    }

    @Override
    public int getItemCount() {
        return mNavTitles.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) return TYPE_HEADER;
        if (isPositionFooter(position)) return TYPE_FOOTER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private boolean isPositionFooter(int position) {
        return position == -1;
    }

}
