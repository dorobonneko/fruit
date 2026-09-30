package moe.mpi;

import android.app.*;
import android.os.*;
import android.widget.ImageView;
import android.view.ViewGroup;
import android.graphics.drawable.ColorDrawable;

public class LibraryActivity extends Activity 
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		ImageView iv=new ImageView(this);
		iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        setContentView(iv,new ViewGroup.LayoutParams(-1,-1));
		//Mpi.$(iv).load("https://imgsa.baidu.com/forum/pic/item/4134970a304e251f858db3ede186c9177f3e53a8.jpg").execute().placeHolder(new ColorDrawable(0xffff0000)).error(new ColorDrawable(0xff00ff00)).into(iv);
		//Mpi.$(iv).load("https://qq.gxgif.com/tp/qa/202509242127450650.gif").execute().into(iv);
		//Mpi.$(iv).load("https://qq.gxgif.com/tp/zjbq/202112232141369090.gif").execute().into(iv);
		//Mpi.$(iv).load("https://qq.gxgif.com/tp/zjbq/202112232141369090.gif").execute().asBitmap().into(iv);
    }
}
