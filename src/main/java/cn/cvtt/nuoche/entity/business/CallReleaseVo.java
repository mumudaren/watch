package cn.cvtt.nuoche.entity.business;

import java.util.Date;

public class CallReleaseVo {
	private String call_id;
	private String no_a;
	private String no_x;
	private String no_b;
	private String start_time;
	private String ring_time;
	private String answer_time;
	private String release_time;
	private String release_dir;
	private String release_cause;
	private String leg;
	private String recording_file;
	private String voicemail_file;
	private String partner_id_real;
	private String interface_result;
	private String sip_result;

	// 方便后续入库而添加,消息内没有这些参数
	private Date time_start;
	private Date time_ring;
	private Date time_answer;
	private Date time_release;

	private String request_id;

	public String getRequest_id() {
		return request_id;
	}

	public void setRequest_id(String request_id) {
		this.request_id = request_id;
	}

	public String getCall_id() {
		return call_id;
	}

	public void setCall_id(String call_id) {
		this.call_id = call_id;
	}

	public String getNo_a() {
		return no_a;
	}

	public void setNo_a(String no_a) {
		this.no_a = no_a;
	}

	public String getNo_x() {
		return no_x;
	}

	public void setNo_x(String no_x) {
		this.no_x = no_x;
	}

	public String getNo_b() {
		return no_b;
	}

	public void setNo_b(String no_b) {
		this.no_b = no_b;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getAnswer_time() {
		return answer_time;
	}

	public void setAnswer_time(String answer_time) {
		this.answer_time = answer_time;
	}
	
	public String getRing_time() {
		return ring_time;
	}

	public void setRing_time(String ring_time) {
		this.ring_time = ring_time;
	}

	public String getRelease_time() {
		return release_time;
	}

	public void setRelease_time(String release_time) {
		this.release_time = release_time;
	}

	public String getRelease_dir() {
		return release_dir;
	}

	public void setRelease_dir(String release_dir) {
		this.release_dir = release_dir;
	}
	
	public String getRelease_cause() {
		return release_cause;
	}

	public void setRelease_cause(String release_cause) {
		this.release_cause = release_cause;
	}
	
	public String getLeg() {
		return leg;
	}

	public void setLeg(String leg) {
		this.leg = leg;
	}

	public String getRecording_file() {
		return recording_file;
	}

	public void setRecording_file(String recording_file) {
		this.recording_file = recording_file;
	}

	public String getVoicemail_file() {
		return voicemail_file;
	}

	public void setVoicemail_file(String voicemail_file) {
		this.voicemail_file = voicemail_file;
	}
	
	public Date getTime_start() {
		return time_start;
	}

	public void setTime_start(Date time_start) {
		this.time_start = time_start;
	}

	public Date getTime_ring() {
		return time_ring;
	}

	public void setTime_ring(Date time_ring) {
		this.time_ring = time_ring;
	}

	public Date getTime_answer() {
		return time_answer;
	}

	public void setTime_answer(Date time_answer) {
		this.time_answer = time_answer;
	}

	public Date getTime_release() {
		return time_release;
	}

	public void setTime_release(Date time_release) {
		this.time_release = time_release;
	}

	public String getPartner_id_real() {
		return partner_id_real;
	}

	public void setPartner_id_real(String partner_id_real) {
		this.partner_id_real = partner_id_real;
	}

	public String getInterface_result() {
		return interface_result;
	}

	public void setInterface_result(String interface_result) {
		this.interface_result = interface_result;
	}

	public String getSip_result() {
		return sip_result;
	}

	public void setSip_result(String sip_result) {
		this.sip_result = sip_result;
	}



	@Override
	public String toString() {
		return "CallReleaseVo{" +
				"call_id='" + call_id + '\'' +
				", no_a='" + no_a + '\'' +
				", no_x='" + no_x + '\'' +
				", no_b='" + no_b + '\'' +
				", start_time='" + start_time + '\'' +
				", ring_time='" + ring_time + '\'' +
				", answer_time='" + answer_time + '\'' +
				", release_time='" + release_time + '\'' +
				", release_dir='" + release_dir + '\'' +
				", release_cause='" + release_cause + '\'' +
				", leg='" + leg + '\'' +
				", recording_file='" + recording_file + '\'' +
				", voicemail_file='" + voicemail_file + '\'' +
				", partner_id_real='" + partner_id_real + '\'' +
				", interface_result='" + interface_result + '\'' +
				", sip_result='" + sip_result + '\'' +
				", time_start=" + time_start +
				", time_ring=" + time_ring +
				", time_answer=" + time_answer +
				", time_release=" + time_release +
				", request_id='" + request_id + '\'' +
				'}';
	}
}
