package me.catzy44.tools.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class EndiAPIConnector {
	private Thread thread;

	private BufferedReader in;
	private PrintWriter out;

	private boolean connected = false;
	private boolean cleanedUp = false;

	private Gson gson = new Gson();

	private Socket socket;

	private Thread keeperThread;
	
	private EndiAPIConnector connector;

	public void enable() {
		connector = this;
		keeperThread = new Thread(new Runnable() {
			@Override
			public void run() {
				out: while (!Thread.interrupted()) {
					connect();
					while ((!cleanedUp || connected) && !Thread.interrupted()) {
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e) {
							break out;
						}
					}
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						break out;
					}
				}
			}
		});
		keeperThread.start();
	}

	public void disable() {
		if (keeperThread != null) {
			keeperThread.interrupt();
		}
		keeperThread = null;
		disconnect();
	}

	private void connect() {
		cleanedUp = false;
		try {
			socket = new Socket("127.0.0.1", 621);
			socket.setSoTimeout(5000);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
		} catch (Exception e) {
			// e.printStackTrace();
			disconnect();
			return;
		}
		connected = true;
		EndiAPI.log("Socket connected!");

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted() && connected) {
					try {
						// if (in.ready()) {
						String input = in.readLine();
						if (input.equals("keepAlive")) {
							out.println("keepAlive");
							continue;
						}
						processInput(input);
						// }
					} catch (Exception e) {
						EndiAPI.log("Socket crashed! [" + e.toString() + "]");
						disconnect();
					}
				}
			}
		});
		thread.start();
	}

	/* packets */

	class Response {
		public JsonObject response;
		public int id;
		public boolean receivedResponse;

		public Response(int id) {
			this.id = id;
		}
	}

	private List<Response> responses = new ArrayList<Response>();

	public JsonObject sendAndWaitForResponse(String cmd, JsonObject value) {
		try {
			int id = generateIdForPacket();

			JsonObject capsule = new JsonObject();
			capsule.addProperty("cmd", cmd);
			capsule.addProperty("id", id);
			capsule.add("value", value);

			// responses.put(id, new Object());
			responses.add(new Response(id));

			String json = gson.toJson(capsule);
			out.println(json);

			int timeout = 5000;// ms
			int vary = 500;
			long time = System.currentTimeMillis();
			while ((System.currentTimeMillis() - time) < timeout && !responses.get(id).receivedResponse) {
				Response res = responses.get(id);
				synchronized (res) {
					res.wait(vary);
				}
			}
			if (!responses.get(id).receivedResponse) {
				return null;
			}
			return responses.get(id).response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void send(String cmd, JsonObject value) {
		JsonObject capsule = new JsonObject();
		capsule.addProperty("cmd", cmd);
		capsule.add("value", value);
		String json = gson.toJson(capsule);
		out.println(json);
	}
	
	public void sendReply(int replyId, JsonObject value) {
		JsonObject capsule = new JsonObject();
		capsule.addProperty("reply", replyId);
		capsule.add("value", value);
		String json = gson.toJson(capsule);
		out.println(json);
	}

	/** packet tracker */

	private int packetIdAI = 0;
	private Lock packerIdAILock = new ReentrantLock();

	public int generateIdForPacket() {
		packerIdAILock.lock();
		int toReturn = packetIdAI;
		packetIdAI++;
		packerIdAILock.unlock();
		return toReturn;
	}

	/* inc packet handling */

	public void addHandler(PacketHandler ph) {
		handlers.add(ph);
	}

	private List<PacketHandler> handlers = new ArrayList<PacketHandler>();

	private void processInput(String s) {
		JsonObject obj = gson.fromJson(s, JsonObject.class);
		
		if(obj.get("reply") != null) {//its reply for message that we sent!
			int reply = obj.get("reply").getAsInt();
			
			for (Response r : responses) {
				if (r.id != reply) {
					continue;
				}
				synchronized (r) {
					r.response = obj.get("value").getAsJsonObject();
					r.receivedResponse = true;
					r.notifyAll();
				}
				return;
			}
		}
		
		String cmd = obj.get("cmd").getAsString();
		for (PacketHandler p : handlers) {
			if (!p.getCmdName().equals(cmd)) {
				continue;
			}

			if(obj.get("id") != null) {//someone sent us message and wants reply!
				int id = obj.get("id").getAsInt();
				JsonObject reply = p.reply(obj.get("value") == null ? null : obj.get("value").getAsJsonObject(), connector);
				
				if(reply == null) {
					return;
				}
				
				sendReply(id,reply);
			} else {
				p.process(obj.get("value") == null ? null : obj.get("value").getAsJsonObject(), connector);
			}
			return;
		}

	}

	public void disconnect() {
		connected = false;
		try {
			thread.interrupt();
		} catch (Exception e) {
		}
		try {
			out.close();
		} catch (Exception e) {
		}
		out = null;
		try {
			in.close();
		} catch (Exception e) {
		}
		in = null;
		try {
			socket.close();
		} catch (Exception e) {
		}
		socket = null;
		cleanedUp = true;
	}

	public BufferedReader getIn() {
		return in;
	}

	public PrintWriter getOut() {
		return out;
	}

	public boolean isConnected() {
		return connected;
	}

}
