//package aloksharma.ufl.edu.stash;
//
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter
//        .ViewHolder> {
//
//    private static final int TYPE_HEADER = 0;
//    private static final int TYPE_ITEM = 1;
//    private static final int TYPE_FOOTER = -1;
//
//    private String mNavTitles[];
//    private int mIcons[];
//
//    private String name;
//    private int profile;
//    private String email;
//
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        int Holderid;
//
//        TextView textView;
//        ImageView imageView;
//        ImageView profile;
//        TextView Name;
//        TextView email;
//
//
//        public ViewHolder(View itemView, int ViewType) {
//            super(itemView);
//
//
//            if (ViewType == TYPE_ITEM) {
//                textView = (TextView) itemView.findViewById(R.id.rowText);
//
//                imageView = (ImageView) itemView.findViewById(R.id.rowIcon);
//
//                Holderid = 1;
//
//            } else {
//                Name = (TextView) itemView.findViewById(R.id.name);
//
//                email = (TextView) itemView.findViewById(R.id.email);
//
//                profile = (ImageView) itemView.findViewById(R.id.circleView)
//                ;
//                Holderid = 0;
//            }
//        }
//
//    }
//
//    CustomAdapter(String Titles[], int Icons[], String Name, String Email,
//                  int Profile) {
//        mNavTitles = Titles;
//        mIcons = Icons;
//        name = Name;
//        email = Email;
//        profile = Profile;
//    }
//
//    @Override
//    public CustomAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int
//            viewType) {
//
//        if (viewType == TYPE_ITEM) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R
//                    .layout.item_row, parent, false);
//
//            ViewHolder vhItem = new ViewHolder(v, viewType);
//            Log.d("stash", "returning row item");
//            return vhItem;
//
//        } else if (viewType == TYPE_HEADER) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R
//                    .layout.header, parent, false);
//
//            ViewHolder vhHeader = new ViewHolder(v, viewType);
//            Log.d("stash", "returning header");
//
//            return vhHeader;
//
//        } else if (viewType == TYPE_FOOTER) {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R
//                    .layout.footer, parent, false);
//
//            ViewHolder vhFooter = new ViewHolder(v, viewType);
//            Log.d("stash", "returning footer");
//
//            return vhFooter;
//        }
//        Log.d("stash", "returning null from CA line 99");
//
//        return null;
//
//    }
//
//    @Override
//    public void onBindViewHolder(CustomAdapter.ViewHolder holder, int
//            position) {
//        if (holder.Holderid == 1) {
//            holder.textView.setText(mNavTitles[position - 1]);
//            holder.imageView.setImageResource(mIcons[position - 1]);
//        } else {
//            holder.profile.setImageResource(profile);
//            holder.Name.setText(name);
//            holder.email.setText(email);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return mNavTitles.length + 1;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (isPositionHeader(position))
//            return TYPE_HEADER;
//
//        if (isPositionFooter(position))
//            return TYPE_FOOTER;
//
//        return TYPE_ITEM;
//    }
//
//    private boolean isPositionHeader(int position) {
//        return position == 0;
//    }
//
//    private boolean isPositionFooter(int position) {
//        return position == -1;
//    }
//
//}
