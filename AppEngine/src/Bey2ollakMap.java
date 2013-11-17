
// My OAuth 2.0 Implementations:
import yoga1290.facebook;
import yoga1290.Foursquare;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jasper.compiler.Node.GetProperty;

import com.google.appengine.api.datastore.*;
import com.google.appengine.labs.repackaged.org.json.*;

public class map
{
	protected static final String fbAppID="326905200782417",fbAppAccessToken="***",fbAppSecret="***";
	private static String latestNews="facebook.com/Bey2ollakMap";
	public static final String days[]=new String[]	{	"Sunday",
		"Monday",
		"Tuesday",
		"Wednesday",
		"Thursday",
		"Friday",
		"Saturday"
	};
	public static BigInteger R=new BigInteger("100");

	private static String newFB(String access_token,String id)
	{
		try
		{
			JSONObject userdata=new JSONObject(facebook.getUser(access_token));
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Entity member=new Entity(KeyFactory.createKey("DATASTORE*", id ));
//			member.setProperty("access_token", access_token);
			member.setProperty("fbid", userdata.getString("id"));
			member.setProperty("genDate", Calendar.getInstance().getTime());
			member.setProperty("name", userdata.getString("name"));
//			datastore.put(member);
			
			member.setProperty("access_token",facebook.refreshAccessToken(fbAppID, fbAppSecret, access_token));
			datastore.put(member);
			return facebook.postNotification( fbAppAccessToken, userdata.getString("id"), userdata.getString("name")+",Thank you for using Bey2ollak Map", "direct?href="+latestNews);
		}catch(Exception e){return e.toString();}
	}
	private static String getFBAccessToken(String id) throws Exception
	{
		return ((String)
				DatastoreServiceFactory.getDatastoreService().get(KeyFactory.createKey("DATASTORE*", id)).getProperty("access_token")
				);
	}
	private static String notifyAll(String message,String href,int offset)
	{
		Query q = new Query("DATASTORE*");
		PreparedQuery pq = DatastoreServiceFactory.getDatastoreService().prepare(q);
		int i,cnt=pq.countEntities(com.google.appengine.api.datastore.FetchOptions.Builder.withLimit(1000));
		List<Entity> res= pq.asList(com.google.appengine.api.datastore.FetchOptions.Builder.withLimit(1000));
		String txt="";
		Entity cur;
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		for(i=offset;i<Math.min(10+offset, cnt);i++)
		{
			cur=res.get(i);
			txt+="<a href='http://fb.me/"+(String)cur.getProperty("fbid")+"'>"+(String)cur.getProperty("fbid")+"</a>:<br>\n"+facebook.postNotification( fbAppAccessToken, (String)cur.getProperty("fbid"), message, "direct?href="+href)+"<br>\n";
		}
		return txt;
	}
	public static void calculateNewAvg(JSONObject data,Entity node,HttpServletResponse debug)
	{
		try
		{
			Calendar C=Calendar.getInstance();
			String AVG[]=((String)node.getProperty(days[C.get(Calendar.DAY_OF_WEEK)-1])).split(",");
			String N[]=((String)node.getProperty(days[C.get(Calendar.DAY_OF_WEEK)-1]+"N")).split(",");
			AVG[C.get(Calendar.HOUR_OF_DAY)-1]=""+
					((
						Double.parseDouble(AVG[C.get(Calendar.HOUR_OF_DAY)-1])*
							Integer.parseInt(N[C.get(Calendar.HOUR_OF_DAY)-1])
					)+ data.getInt("speed") )
					/
					(Integer.parseInt(N[C.get(Calendar.HOUR_OF_DAY)-1])+1);
			N[C.get(Calendar.HOUR_OF_DAY)-1]=""+(Integer.parseInt(N[C.get(Calendar.HOUR_OF_DAY)-1])+1);
			
			String newAVG="",newN="";
			if(AVG.length>0)
			{
				newAVG=AVG[0];
				newN=N[0];
				for(int i=1;i<AVG.length;i++)
				{
					newAVG+=","+AVG[i];
					newN+=","+N[i];
				}
			}
			
			node.setProperty(days[C.get(Calendar.DAY_OF_WEEK)-1]+"" , newAVG);
			node.setProperty(days[C.get(Calendar.DAY_OF_WEEK)-1]+"N" , newN);
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			com.google.appengine.api.datastore.Transaction txn = datastore.beginTransaction();
			datastore.put(node);
			txn.commit();
		}catch(Exception e){
			try{
			debug.getWriter().println("Exception at new AVG:\n"+e.getMessage()+" ; "+e.getLocalizedMessage());}
			catch(Exception e2){}
			}
	}
	public static int check(BigInteger a,BigInteger b,BigInteger R)
	{
		int check1=a.subtract(R).compareTo(b);
		int check2=a.add(R).compareTo(b);
		
		if(check1>0 && check2>0)
			return 1;
		if(check1<0 && check2<0)
			return -1;
		return 0;
	}
	public static Entity insertX(BigInteger x,HttpServletResponse debug) throws Exception
	{
		try
		{
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Entity nnode,cur;
			String order[];
			try{
				order=((Text)(datastore.get(KeyFactory.createKey("X_Node", "order")).getProperty("order"))).getValue().split(",");
			}catch(Exception e){
				order=new String[]{};
			}
			int s=0,e=order.length-1,mid=-2,omid=-1,check=-1,selectedIndex=0,i;
			boolean replace=false;
			while(s<=e && omid!=mid)
			{
				omid=mid;
				mid=(s+e)/2;
				cur=datastore.get(KeyFactory.createKey("X_Node", Integer.parseInt(order[mid])		));
				check=check(new BigInteger((String)	cur.getProperty("x"))	,x,R);
				if(check==0)
				{
					replace=true;
					selectedIndex=mid;
					break;
				}
				else if(check<0)
					s=mid;
				else
					e=mid;
			}
			String newOrder="";
			if(replace)
			{
				nnode=datastore.get(KeyFactory.createKey("X_Node", Integer.parseInt(order[selectedIndex]) ));
			}
			else
			{
				nnode=new Entity(KeyFactory.createKey("X_Node", order.length+1));
				nnode.setProperty("x",x.toString());
				for(i=0;i<order.length;i++)
					if(i==mid)
					{
						if(check<0)
							newOrder+=","+(order.length+1)+","+order[i];
						else
							newOrder+=","+order[i]+","+(order.length+1);
					}
					else
						newOrder+=","+order[i];
				if(newOrder.length()==0)
					newOrder+=","+(order.length+1)+"";
				newOrder=newOrder.substring(1);
			}
			if(!replace)
			{
				datastore.put(nnode);
				com.google.appengine.api.datastore.Transaction txn = datastore.beginTransaction();
				Entity newOrderNode=new Entity(KeyFactory.createKey("X_Node", "order"));
				newOrderNode.setProperty("order", new Text(newOrder));
				datastore.put(newOrderNode);
				txn.commit();
			}
			return nnode;
		}catch(Exception e){debug.getWriter().println("<br>insertX:"+e.getMessage()+"<br>");return null;}
	}
	
	public static Entity insert(BigInteger x,BigInteger y,String data,HttpServletResponse debug) throws Exception
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Entity nnode,cur,parentX=insertX(x,debug);
		String order[];
		try{
			order=((Text)(datastore.get(KeyFactory.createKey(parentX.getKey(),"Y_Node", "order")).getProperty("order"))).getValue().split(",");
		}catch(Exception e){
			order=new String[]{};
		}
		int s=0,e=order.length-1,mid=-2,omid=-1,check=-1,selectedIndex=0,i;
		boolean replace=false;
		while(s<=e && omid!=mid)
		{
			omid=mid;
			mid=(s+e)/2;
			cur=datastore.get(KeyFactory.createKey(parentX.getKey(),"Y_Node", Integer.parseInt(order[mid])		));
			check=check(new BigInteger((String)	cur.getProperty("y"))	,y,R);
			if(check==0)
			{
				replace=true;
				selectedIndex=mid;
				break;
			}
			else if(check<0)
				s=mid;
			else
				e=mid;
		}
		String newOrder="";
		if(replace)
		{
			nnode=datastore.get(KeyFactory.createKey(parentX.getKey(),"Y_Node", (long) Integer.parseInt(order[selectedIndex]) ));
			calculateNewAvg(new JSONObject(data),nnode,debug);
		}
		else
		{
			nnode=new Entity(KeyFactory.createKey(parentX.getKey(),"Y_Node", order.length+1));
			nnode.setProperty("x",x.toString());
			nnode.setProperty("y",y.toString());
			for(i=0;i<days.length;i++)
			{
				nnode.setProperty(days[i],"0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
				nnode.setProperty(days[i]+"N","0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0");
			}
			calculateNewAvg(new JSONObject(data),nnode,debug);			
			for(i=0;i<order.length;i++)
				if(i==mid)
				{
					if(check<0)
						newOrder+=","+(order.length+1)+","+order[i];
					else
						newOrder+=","+order[i]+","+(order.length+1);
				}
				else
					newOrder+=","+order[i];
			
			if(newOrder.length()==0)
				newOrder+=","+(order.length+1)+"";
			newOrder=newOrder.substring(1);
		}
		
		if(!replace)
		{
			Entity newOrderNode=new Entity(KeyFactory.createKey(
					parentX.getKey(),"Y_Node", "order"));
			newOrderNode.setProperty("order", newOrder);
			datastore.put(newOrderNode);
		}
		return nnode;
	}

	public static String getDataXY(BigInteger x,BigInteger y) // throws Exception
	{
		String debug="";
		try{
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Entity nnode,cur;
			String order[];
				order=((Text)(datastore.get(KeyFactory.createKey("X_Node", "order")).getProperty("order"))).getValue().split(",");
			
				debug+="OrderX length="+order.length+"\n<br>";
			int s=0,e=order.length-1,mid=-1,omid=0,check,selectedIndex=0,i;
			boolean replace=false;
			while(s<=e && omid!=mid)
			{
				omid=mid;
				mid=(s+e)/2;
				debug+="s="+s+", e="+e+"\n<br>";
				debug+="midX="+mid+", order="+order[mid]+"\n<br>";

				
				cur=datastore.get(KeyFactory.createKey("X_Node", Integer.parseInt(order[mid])		));
				
				check=check(new BigInteger((String)	cur.getProperty("x"))	,x,R);
				if(check==0)
				{
					selectedIndex=mid;
					break;
				}
				else if(check<0) //d<get(m)
				{
					s=mid;
					selectedIndex=mid;
				}
				else	
					e=mid;
			
			}
			Entity parentX=datastore.get(KeyFactory.createKey("X_Node", Integer.parseInt(order[selectedIndex]) ));
			order=((Text)(datastore.get(KeyFactory.createKey(parentX.getKey(), "Y_Node", "order")).getProperty("order"))).getValue().split(",");
			//2nd Binary search in Y-axis
			s=0;
			e=order.length-1;
			mid=-1;omid=0;selectedIndex=0;
			debug+="OrderY len="+order.length+"<br>\n";
			while(s<=e && omid!=mid)
			{
				omid=mid;
				mid=(s+e)/2;
				debug+="s="+s+", e="+e+"\n<br>";
				debug+="midY="+mid+", order="+order[mid]+"\n<br>";
				
				cur=datastore.get(KeyFactory.createKey(parentX.getKey(),"Y_Node", Integer.parseInt(order[mid])		));
				check=check(new BigInteger((String)	cur.getProperty("y"))	,y,R);
				if(check==0)
				{
//					replace=true;
					selectedIndex=mid;
					break;
				}
				else if(check<0) //d<get(m)
				{
					s=mid;
					selectedIndex=mid;
				}
				else					
					e=mid;
				
			}
			return	new JSONObject(
					datastore.get(KeyFactory.createKey(parentX.getKey(),"Y_Node",(long) Integer.parseInt(order[selectedIndex])	)).getProperties()
					).toString();
		}catch(Exception e){
			return debug+"<br>getDataXY:"+e.getMessage();
		}
	}
	public static String FixX()
	{
		String ret="";
		class node
		{
			String id,value;
			node(String id,String value)
			{
				this.id=id;
				this.value=value;
			}
		}
		try
		{
			Query q = new Query("X_Node");
			PreparedQuery pq = DatastoreServiceFactory.getDatastoreService().prepare(q);
			int i,cnt=pq.countEntities(com.google.appengine.api.datastore.FetchOptions.Builder.withLimit(1000));
			List<Entity> res= pq.asList(com.google.appengine.api.datastore.FetchOptions.Builder.withLimit(1000));
			String order="";
			
			TreeSet<node> ts=new TreeSet<node>(
									new Comparator<node>()
										{
											@Override
											public int compare(node a,node b)
											{
												if(Long.parseLong(a.value)== Long.parseLong(b.value) )	return 0;
												if(Long.parseLong(a.value)< Long.parseLong(b.value) ) return -1;
												return 1;
											}
										});
			for(i=0;i<cnt;i++)
			{
				Entity cur=res.get(i);
				if(cur.hasProperty("x"))
					ts.add(new node(cur.getKey().getId()+"",(String)cur.getProperty("x")));
			}
			node sorted[]=new node[ts.size()];
			ts.toArray(sorted);
			
			order=sorted[0].id;
			for(i=1;i<sorted.length;i++)
				if(sorted[i].value==sorted[i-1].value)
					ret+=","+sorted[i].id;
				else
					order+=","+sorted[i].id; //+"\t , "+sorted[i].value+"\n<br>";
			ret+="\n<br>"+order;
			
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			Entity newOrderNode=new Entity("X_Node", "order");
			newOrderNode.setProperty("order", new Text(order));
			datastore.put(newOrderNode);
		}catch(Exception e){return ret+"<br>Error:"+e.toString();}
		return ret;
	}
	private static String FixY()
	{
		String ret="";
		try
		{
			TreeSet<Entity> ts=new TreeSet<Entity>(
									new Comparator<Entity>()
										{
											@Override
											public int compare(Entity a,Entity b)
											{
												int x=Integer.parseInt((String)a.getProperty("x"));
												int y=Integer.parseInt((String)a.getProperty("y"));
												int x2=Integer.parseInt((String)b.getProperty("x"));
												int y2=Integer.parseInt((String)b.getProperty("y"));
//												if(x==x2 && y==y2)	return 0;
												if(x==x2)
												{
													
													if(y==y2)	return 0;
													if(y< y2) return 1;
												}
												else if(y==y2)
												{
													if(x== x2 )	return 0;
													if(x< x2 ) return 1;
												}
												return -1;
											}
										});
			Query q = new Query("Y_Node");
			PreparedQuery pq = DatastoreServiceFactory.getDatastoreService().prepare(q);
			int i,cnt=pq.countEntities(com.google.appengine.api.datastore.FetchOptions.Builder.withLimit(1000));
			List<Entity> res= pq.asList(com.google.appengine.api.datastore.FetchOptions.Builder.withLimit(1000));
			String order="";
			Entity cur;
			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			for(i=0;i<cnt;i++)
			{
				cur=res.get(i);
				if(cur.getProperty("x")!=null && cur.getProperty("y")!=null)
					ts.add(res.get(i));
			}
			Iterator<Entity> it=ts.iterator();
			String oldX=null;
			Key oldParent=null;
			String oldOrder="";
			while(it.hasNext())
			{
				cur=it.next();
				
				if(oldParent==null)
				{
					oldParent=cur.getParent();
					oldOrder=cur.getKey().getId()+"";
					ret+=new JSONObject(cur.getProperties()).toString();
				}
				else if(cur.getParent().getId()!=oldParent.getId())
				{
					com.google.appengine.api.datastore.Transaction txn = datastore.beginTransaction();
					Entity newOrderNode=new Entity(KeyFactory.createKey(
							oldParent,"Y_Node", "order"));
					newOrderNode.setProperty("order", new Text(oldOrder));
					datastore.put(newOrderNode);
					txn.commit();
					//TODO
					oldOrder=cur.getKey().getId()+"";
					oldParent=cur.getParent();
					ret+="\n<br><br>\n"+new JSONObject(cur.getProperties()).toString();
				}
				else if(cur.getParent().getId()==oldParent.getId())
				{
					oldOrder+=","+cur.getKey().getId();
					ret+="\n<br>\n"+new JSONObject(cur.getProperties()).toString();
				}
			}
			if(oldParent!=null)
			{
				com.google.appengine.api.datastore.Transaction txn = datastore.beginTransaction();
				Entity newOrderNode=new Entity(KeyFactory.createKey(
						oldParent,"Y_Node", "order"));
				newOrderNode.setProperty("order", new Text(oldOrder));
				datastore.put(newOrderNode);
				txn.commit();
			}
			
		}catch(Exception e){return ret+"<br>Error:"+e.toString();}
		return ret;
	}
	//Binary Search in the Y-axis for the given parentX
	public static String getDataBetween(Entity parentX,BigInteger y, BigInteger R2,HttpServletResponse debug) throws Exception
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String order[]=((Text)(datastore.get(KeyFactory.createKey(parentX.getKey(),"Y_Node", "order")).getProperty("order"))).getValue().split(",");
		BigInteger x=new BigInteger((String)parentX.getProperty("x"));
		//Lower boundry Binary search in Y-axis
		
		int s=0;
		int e=order.length-1;
		int mid=-1,omid=0,lowerIndex=0,check;
		Entity cur;
//		debug.getWriter().println("<br>getDataBetween("+parentX.getKey()+","+y+"):<br>\n");
		while(s<=e && omid!=mid)
		{
			omid=mid;
			mid=(s+e)/2;
			
			cur=datastore.get(KeyFactory.createKey(parentX.getKey(),"Y_Node", Integer.parseInt(order[mid])		));
			check=new BigInteger((String)	cur.getProperty("y")).compareTo(y.subtract(R2));
//			debug.getWriter().println("Y1: "+y.subtract(R2)+" [<,=,>] "+(String)cur.getProperty("y")+" = "+check+"<br>\n");
			
			if(check==0)
			{
				lowerIndex=mid;
				break;
			}
			else if(check<0)
			{
//				lowerIndex=mid;
				s=mid;
			}
			else
			{
				lowerIndex=mid;//
				e=mid;
			}
		}
		// binary search
		s=0;
		e=order.length-1;
		mid=-1;omid=0;
		int higherIndex=0;
		
		while(s<=e && omid!=mid)
		{
			omid=mid;
			mid=(s+e)/2;
			
			cur=datastore.get(KeyFactory.createKey(parentX.getKey(),"Y_Node", Integer.parseInt(order[mid])		));
			check=new BigInteger((String)	cur.getProperty("y")).compareTo(y.add(R2));
			if(check==0)
			{
				higherIndex=mid;
				break;
			}
			else if(check<0)
			{
				higherIndex=mid;//
				s=mid;
			}
			else
			{
//				higherIndex=mid;
				e=mid;
			}
		}
		
		String res="";
			for(int i=lowerIndex;i<=higherIndex;i++)
				res+=
					new JSONObject(
							 datastore.get(KeyFactory.createKey(parentX.getKey(),"Y_Node",	Integer.parseInt(order[i]) 	)).getProperties() 
					).toString()+(i==higherIndex ? "":"\n");
		return res;
	}
	
	
	public static String getDataBetween(BigInteger x,BigInteger y, BigInteger R2 , HttpServletResponse debug) throws Exception
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String order[]=((Text)(datastore.get(KeyFactory.createKey("X_Node", "order")).getProperty("order"))).getValue().split(",");
		//Lower boundry Binary search in Y-axis
		
		int s=0;
		int e=order.length-1;
		int mid=-1,omid=0,lowerIndex=0,check;
		Entity cur;
		
		while(s<=e && omid!=mid)
		{
			omid=mid;
			mid=(s+e)/2;
			cur=datastore.get(KeyFactory.createKey("X_Node", Integer.parseInt(order[mid])		));
			check=new BigInteger((String)	cur.getProperty("x")).compareTo(x.subtract(R2));
			if(check==0)
				lowerIndex=mid;
			else if(check<0)
			{
				s=mid;
//				lowerIndex=mid;
			}
			else 
			{
				e=mid;
				lowerIndex=mid;//
			}
		}
		// binary search
		s=0;
		e=order.length-1;
		mid=-1;omid=0;
		int higherIndex=0;
		
		
		while(s<=e && omid!=mid)
		{
			omid=mid;
			mid=(s+e)/2;
			cur=datastore.get(KeyFactory.createKey("X_Node", Integer.parseInt(order[mid])		));
			check=new BigInteger((String)	cur.getProperty("x")).compareTo(x.add(R2));
			if(check==0) //almost impossible/very rarely to happen
				higherIndex=mid;
			else if(check<0)
			{
				s=mid;
				higherIndex=mid;//
			}
			else
			{
				e=mid;
//				higherIndex=mid;
			}
		}
		
		String res="";
			for(int i=lowerIndex;i<=higherIndex;i++)
				try{
				res+=getDataBetween(
						datastore.get(KeyFactory.createKey("X_Node", Integer.parseInt(order[i])))
							, y, R2,debug )+(i==higherIndex ? "":"\n");
				}catch(Exception e2){}
		return res;
	}
	
	
	
	
	public static void doGet(HttpServletRequest req, HttpServletResponse resp)
	{
		try{
			if(req.getRequestURI().equals(“/GET))
			{
				BigInteger x=new BigInteger(req.getParameter("x"));
				BigInteger y=new BigInteger(req.getParameter("y"));
				String r=req.getParameter("r");
				if(r!=null)
					resp.getWriter().println(getDataBetween(x, y, new BigInteger(r),resp));
				else
					resp.getWriter().println(getDataXY(x, y));
			}
			else if(req.getRequestURI().equals("/getFBAccessTokenByDevice”))
			{
				resp.getWriter().println(getFBAccessToken(req.getParameter("id")));
			}
			else if(req.getRequestURI().equals("/OAuth_REDIRECT_URI/4sqr/“))
			{
				//redirected from https://foursquare.com/oauth2/authenticate?client_id=YKX5JWIH3CLHG1JHDSS4EBZEGGWFTUQNTBGZF3PGZZONYX3M&response_type=code&redirect_uri=OAuth_REDIRECT_URI/foursquare/
				resp.getWriter().println(
						Foursquare.getAccessToken("YKX5JWIH3CLHG1JHDSS4EBZEGGWFTUQNTBGZF3PGZZONYX3M",
								“****”,
								"OAuth_REDIRECT_URI/4sqr/“, req.getParameter("code"))
								);
			}
			else if(req.getRequestURI().equals(“/FIX”))
			{
					resp.getWriter().println(FixX());
					resp.getWriter().println(FixY());
			}
			else if(req.getRequestURI().equals(“OAuth_REDIRECT_URI/fb/”))
			{
				String code=req.getParameter("code");
				String state=req.getParameter("state");
				String access_token=facebook.getAccessToken("326905200782417", "***", "OAuth_REDIRECT_URI/fb/“, code);
				
				
				newFB(access_token, state);
				resp.getWriter().println("<script>alert(\"I'll send you notification on the next update :) \");top.location.href=location.protocol+'//"+latestNews+"';</script>");
			}
			
			else if(req.getRequestURI().equals(“/FACEBOOK_NOTIFICATIONS“))
			{
				resp.getWriter().println(notifyAll("تحب زرار يعرف صحابك عن كفاحك في الطريق؟ ºしº",
						"direct?href=473798436070804", 
						Integer.parseInt(req.getParameter("o"))
						));
			}
		
		}catch(Exception e){
			
			try {
				resp.getWriter().println(e.getMessage());
			} catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
	public static void doPost(HttpServletRequest req, HttpServletResponse resp)
	{
		try{
			if(req.getRequestURI().equals("/direct"))
			{
				String d="";
				int i;
				String href=req.getParameter("href");
				// direct to some photo id when the Facebook notification is pressed
				resp.getWriter().write("Please wait...<script>top.location.href=location.protocol+'//www.facebook.com/photo.php?fbid="+href+"';</script>");
			}
			else if(req.getRequestURI().equals(“/SET“))
			{
				
				BigInteger x=new BigInteger(req.getParameter("x"));
				BigInteger y=new BigInteger(req.getParameter("y"));
				String r=req.getParameter("r");
				
				InputStream in=req.getInputStream();
				int i;
				String d="";
				byte buff[]=new byte[200];
				while((i=in.read(buff))>0)
					d+=new String(buff,0,i);
				in.close();
				
				insert(x, y, d,resp);
				resp.getWriter().println(getDataBetween(x, y, new BigInteger(r),resp));
			}
			
		}catch(Exception e){
			try{
				resp.getWriter().println(e);
			}catch(Exception e2){}
			
		}
	}
}
