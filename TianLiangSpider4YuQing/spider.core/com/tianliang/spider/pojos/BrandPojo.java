package com.tianliang.spider.pojos;

/**
 * 商标采集的pojo类
 * 
 * @author zhouerliang
 *
 */
public class BrandPojo {
	@Override
	public String toString() {
		return "BrandPojo [\n image_url=" + image_url + "\n name=" + name
				+ "\n register_or_apply_number=" + register_or_apply_number
				+ "\n classify_number=" + classify_number + "\n apply_date="
				+ apply_date + "\n classify_level=" + classify_level
				+ "\n product_or_service_content=" + product_or_service_content
				+ "\n apply_color=" + apply_color + "\n is_common_use="
				+ is_common_use + "\n international_register_date="
				+ international_register_date + "\n after_specify_date="
				+ after_specify_date + "\n priority_date=" + priority_date
				+ "\n apply_username_chinese=" + apply_username_chinese
				+ "\n apply_address_chinese=" + apply_address_chinese
				+ "\n apply_username_english=" + apply_username_english
				+ "\n apply_address_english=" + apply_address_english
				+ "\n agent_company=" + agent_company + "\n progress_apply_date="
				+ progress_apply_date
				+ "\n progress_first_check_publish_number="
				+ progress_first_check_publish_number
				+ "\n progress_regsiter_publish_number="
				+ progress_regsiter_publish_number
				+ "\n progress_first_check_publish_date="
				+ progress_first_check_publish_date
				+ "\n progress_regsiter_publish_date="
				+ progress_regsiter_publish_date
				+ "\n progress_regsiter_have_three_years_publish_date="
				+ progress_regsiter_have_three_years_publish_date
				+ "\n progress_regsiter_date=" + progress_regsiter_date
				+ "\n progress_deadline_date=" + progress_deadline_date
				+ "\n newest_message=" + newest_message + "\n from_source="
				+ from_source + "]";
	}

	//商标图样
	private String image_url;
	//商标名称
	private String name;
	//注册号/申请号
	private String register_or_apply_number;
	//商标类别
	private String classify_number;
	//申请日
	private String apply_date;
	//商标类型
	private String classify_level;
	//商品/服务
	private String product_or_service_content;
	//指定颜色
	private String apply_color;
	//是否共有商标
	private String is_common_use;
	//国际注册日期
	private String international_register_date;
	//后指定日期
	private String after_specify_date;
	//优先权日期
	private String priority_date;
	//申请人名称（中文）
	private String apply_username_chinese;
	//申请人地址（中文）
	private String apply_address_chinese;
	//申请人名称（英文）
	private String apply_username_english;
	//申请人地址（英文）
	private String apply_address_english;
	//代理公司
	private String agent_company;
	//申请日期
	private String progress_apply_date;
	//初审公告期号
	private String progress_first_check_publish_number;
	//注册公告期号
	private String progress_regsiter_publish_number;
	//初审公告日期
	private String progress_first_check_publish_date;
	//注册公告日期
	private String progress_regsiter_publish_date;
	//注册满三年
	private String progress_regsiter_have_three_years_publish_date;
	//注册日期	
	private String progress_regsiter_date;
	//截止日期
	private String progress_deadline_date;
	//商标最新动态
	private String newest_message;
	//该信息来源，如超凡商標管家或標庫網
	private String from_source;

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRegister_or_apply_number() {
		return register_or_apply_number;
	}

	public void setRegister_or_apply_number(String register_or_apply_number) {
		this.register_or_apply_number = register_or_apply_number;
	}

	public String getClassify_number() {
		return classify_number;
	}

	public void setClassify_number(String classify_number) {
		this.classify_number = classify_number;
	}

	public String getApply_date() {
		return apply_date;
	}

	public void setApply_date(String apply_date) {
		this.apply_date = apply_date;
	}

	public String getClassify_level() {
		return classify_level;
	}

	public void setClassify_level(String classify_level) {
		this.classify_level = classify_level;
	}

	public String getProduct_or_service_content() {
		return product_or_service_content;
	}

	public void setProduct_or_service_content(String product_or_service_content) {
		this.product_or_service_content = product_or_service_content;
	}

	public String getApply_color() {
		return apply_color;
	}

	public void setApply_color(String apply_color) {
		this.apply_color = apply_color;
	}

	public String getIs_common_use() {
		return is_common_use;
	}

	public void setIs_common_use(String is_common_use) {
		this.is_common_use = is_common_use;
	}

	public String getInternational_register_date() {
		return international_register_date;
	}

	public void setInternational_register_date(
			String international_register_date) {
		this.international_register_date = international_register_date;
	}

	public String getAfter_specify_date() {
		return after_specify_date;
	}

	public void setAfter_specify_date(String after_specify_date) {
		this.after_specify_date = after_specify_date;
	}

	public String getPriority_date() {
		return priority_date;
	}

	public void setPriority_date(String priority_date) {
		this.priority_date = priority_date;
	}

	public String getApply_username_chinese() {
		return apply_username_chinese;
	}

	public void setApply_username_chinese(String apply_username_chinese) {
		this.apply_username_chinese = apply_username_chinese;
	}

	public String getApply_address_chinese() {
		return apply_address_chinese;
	}

	public void setApply_address_chinese(String apply_address_chinese) {
		this.apply_address_chinese = apply_address_chinese;
	}

	public String getApply_username_english() {
		return apply_username_english;
	}

	public void setApply_username_english(String apply_username_english) {
		this.apply_username_english = apply_username_english;
	}

	public String getApply_address_english() {
		return apply_address_english;
	}

	public void setApply_address_english(String apply_address_english) {
		this.apply_address_english = apply_address_english;
	}

	public String getAgent_company() {
		return agent_company;
	}

	public void setAgent_company(String agent_company) {
		this.agent_company = agent_company;
	}

	public String getProgress_apply_date() {
		return progress_apply_date;
	}

	public void setProgress_apply_date(String progress_apply_date) {
		this.progress_apply_date = progress_apply_date;
	}

	public String getProgress_first_check_publish_number() {
		return progress_first_check_publish_number;
	}

	public void setProgress_first_check_publish_number(
			String progress_first_check_publish_number) {
		this.progress_first_check_publish_number = progress_first_check_publish_number;
	}

	public String getProgress_regsiter_publish_number() {
		return progress_regsiter_publish_number;
	}

	public void setProgress_regsiter_publish_number(
			String progress_regsiter_publish_number) {
		this.progress_regsiter_publish_number = progress_regsiter_publish_number;
	}

	public String getProgress_first_check_publish_date() {
		return progress_first_check_publish_date;
	}

	public void setProgress_first_check_publish_date(
			String progress_first_check_publish_date) {
		this.progress_first_check_publish_date = progress_first_check_publish_date;
	}

	public String getProgress_regsiter_publish_date() {
		return progress_regsiter_publish_date;
	}

	public void setProgress_regsiter_publish_date(
			String progress_regsiter_publish_date) {
		this.progress_regsiter_publish_date = progress_regsiter_publish_date;
	}

	public String getProgress_regsiter_have_three_years_publish_date() {
		return progress_regsiter_have_three_years_publish_date;
	}

	public void setProgress_regsiter_have_three_years_publish_date(
			String progress_regsiter_have_three_years_publish_date) {
		this.progress_regsiter_have_three_years_publish_date = progress_regsiter_have_three_years_publish_date;
	}

	public String getProgress_regsiter_date() {
		return progress_regsiter_date;
	}

	public void setProgress_regsiter_date(String progress_regsiter_date) {
		this.progress_regsiter_date = progress_regsiter_date;
	}

	public String getProgress_deadline_date() {
		return progress_deadline_date;
	}

	public void setProgress_deadline_date(String progress_deadline_date) {
		this.progress_deadline_date = progress_deadline_date;
	}

	public String getNewest_message() {
		return newest_message;
	}

	public void setNewest_message(String newest_message) {
		this.newest_message = newest_message;
	}

	public String getFrom_source() {
		return from_source;
	}

	public void setFrom_source(String from_source) {
		this.from_source = from_source;
	}

}
