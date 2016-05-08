var system = require('system');
var webPage = require('webpage');
var fs = require('fs');
var page = webPage.create();

// page.settings.userAgent = 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/534.34
// (KHTML, like Gecko)Safari/534.34';
page.settings.userAgent = 'Mozilla/5.0 (Windows NT 6.1) AppleWebKit/534.34 (KHTML, like Gecko)Safari/534.34';
// page.settings.resourceTimeout=1000;
// Mozilla/5.0 (Windows NT 6.1) AppleWebKit/534.34 (KHTML, like Gecko)
// PhantomJS/1.9.7 Safari/534.34
// console.log('system.args.length---'+system.args.length);
if (system.args.length < 2) {
	console.log('the parameter is not right!');
	phantom.exit();
}
// 两个预读取参数
var crawl_para_file_path;
var crawl_para_json;

/*
 * 读取出命令行中crawl_para的json参数列表
 */
crawl_para_file_path = system.args[1];

// 将文件中的字符串转换成JSON对象
function getJsonObjByFile(filePath) {
	var config_crawl = fs.read(filePath);
	var obj = JSON.parse(config_crawl);
	return obj;
}

// crawl_para_json=getJsonObjByFile("baiduCrawler/config_crawl_para.json");
crawl_para_json = getJsonObjByFile(crawl_para_file_path);

// 爬虫开始爬的起始路径
var root_url = crawl_para_json.root_url;
// user agent赋值
page.settings.userAgent = crawl_para_json.userAgent;
// 选择是否inject jquery
var is_inject_jquery = crawl_para_json.is_inject_jquery;
// jquery.js的路径
var jquery_path = crawl_para_json.jquery_path;
// 是否对抓取的页面进行capture与保存
var is_capture_pic = crawl_para_json.is_capture_pic;
// 图片要保存到的路径
var pic_capture_save_root_path = crawl_para_json.pic_capture_save_root_path;
// 图片文件的前缀名字
var pic_file_prefix_name = crawl_para_json.pic_file_prefix_name;
// 图片文件的后缀名字
var pic_file_suffix_name = crawl_para_json.pic_file_suffix_name;
// 每个关键字或url任务的指定自己的目录，是root目录的一级目录
var pic_capture_save_sub_path = crawl_para_json.pic_capture_save_sub_path;
// 要抓取的最大页数量
var max_page_number = crawl_para_json.max_page_number;
// 将抓取下的网页的内容写到文件中
var is_data_write_to_file = crawl_para_json.is_data_write_to_file;
// 写入目录的根文件目录
var data_write_to_file_root_path = crawl_para_json.data_write_to_file_root_path;
// 数据目录的前缀名字
var data_file_prefix_name = crawl_para_json.data_file_prefix_name;
// 数据目录的后缀名字
var data_file_suffix_name = crawl_para_json.data_file_suffix_name;
// 文本数据要保存的自己指定的文件目录
var data_write_to_file_sub_path = crawl_para_json.data_write_to_file_sub_path;
// 最长等待无反应时间，如果过了这个时间无反应，则将退出phantomjs或者重新打下某个页面,暂选择退出phantomjs
var no_response_waitting_time_max = crawl_para_json.no_response_waitting_time_max;
// 最多可以重复‘等待超过最长时间’的次数，有可能是网络的原因，故要重复请求一下
var no_response_waitting_fail_time_max = crawl_para_json.no_response_waitting_fail_time_max;
// keyword 定义
var search_keyword = crawl_para_json.search_keyword;

// 图片页数记数
var count_render_pic = 1;
// 抓取到的文本页数记数
var count_crawl_txt = 1;
// 已经抓取过的页面记数，因为上边的图片和文本不一定要启用和记数，故在此独立记录已走过的实际页面
var count_crawl_page_number = 1;
// 标志该page中是否已注入过jquery.js文件了
var inject_jquery_flag = false;

// 对收到的response的计数
var receive_response_count_current = 0;
var receive_response_count_last = 0;
var receive_response_fail_count = 0;
// 现在正在抓取的页面
var current_grab_page_number = 1;

function reinit() {
	current_grab_page_number = 1;
	//添加失败的此数计数
	receive_response_fail_count++
}

page.onAlert = function(msg) {
	console.log('ALERT: ' + msg);
	if (msg == "phantom will exit") {
		exitPhantom();
	}
};

// 周期检查是否请求过程中产生了死掉情况
function checkNoResponse() {
	if (receive_response_count_last == receive_response_count_current) {
		if (receive_response_fail_count < no_response_waitting_fail_time_max) {
			console.log('check fail response,will try again');
			// closePage();
			// exitPhantom();
			reinit();
			openPage(root_url);
		} else {
			console
					.log('check response wait time arrive the max waitting time,phantom will exit!');
			exitPhantom();
		}
	}
	receive_response_count_last = receive_response_count_current;
}

// 启动周期检查有无回应
setInterval(checkNoResponse, no_response_waitting_time_max);

page.onLoadFinished = function(status) {
	//第一次是请求搜索，不算做一页
	if ((current_grab_page_number-1) > max_page_number) {
		console
				.log('the crawled page number is arrived to the max value,will exit phantomjs!');
		exitPhantom();
	}
	console.log('load finish--------');
	console.log('page.title---' + page.title);
	if (is_inject_jquery && !inject_jquery_flag) {
		if (inject_jquery_flag = page.injectJs(jquery_path)) {
			console.log('inject jquery sucessful!');
		} else {
			console.log('inject jquery fail!');
		}
	}
	console.log('page.url----' + page.url);

	// 用于临时测试
	/**
	 * page.render('finish_index'+count+'.png'); count++;
	 * fs.write('page.txt',page.content,'a');
	 * fs.write('page.txt','\n\n********','a');
	 */
	if ((page.url.indexOf("https://www.baidu.com/s?")) > -1) {
		if (is_capture_pic) {
//			page.render(pic_capture_save_root_path + pic_capture_save_sub_path
//					+ pic_file_prefix_name + count_render_pic
//					+ pic_file_suffix_name);
//			count_render_pic++;
			//新的方式
			page.render(pic_capture_save_root_path + pic_capture_save_sub_path
					+ pic_file_prefix_name + current_grab_page_number
					+ pic_file_suffix_name);
			
		}

		// 过滤指定内容，并写入到文件中
		/*
		 * var aid_content_txt = page.evaluate(function() { return
		 * $('#content_left').html(); });
		 */

		// 是否将抓取到的数据每页写入一个文本文件,其编页为output-encoding
		if (is_data_write_to_file) {
//			fs.write(data_write_to_file_root_path + data_write_to_file_sub_path
//					+ data_file_prefix_name + count_crawl_txt
//					+ data_file_suffix_name, page.content, 'a');
//			count_crawl_txt++;
			//新的方式，以翻页为单位
			fs.write(data_write_to_file_root_path + data_write_to_file_sub_path
					+ data_file_prefix_name + (current_grab_page_number-1)
					+ data_file_suffix_name, page.content, 'a');
		}
		// 处理翻页
		page.evaluate(function(current_grab_page_number_copy) {
			// var arr = document.querySelectorAll('a[class="n"]');
			// var arr = document.querySelectorAll('p#page>a');
			var arr = document.querySelectorAll('div#page>a.n');
			
			var ev = document.createEvent("MouseEvents");
			ev.initEvent("click", false, true);
//			window.alert('arr--' + arr.length);
			if (arr.length == 0) {
				window.alert('crawl is arriveing the end,will be exit');
				window.alert('phantom will exit');
			} else if (arr.length == 1) {
//				window.alert(arr[0].innerHTML);
				if (arr[0].innerHTML.indexOf("下一页") > -1) {
//					window.alert('next page-start');
					arr[0].dispatchEvent(ev);
//					window.alert('next page-end');
				}
			} else if (arr.length == 2) {
//				window.alert(arr[1].innerHTML);
				if (arr[1].innerHTML.indexOf("下一页") > -1) {
//					window.alert('next page-start');
					arr[1].dispatchEvent(ev);
//					window.alert('next page-end');
				}
			}
//			window.alert('current_grab_page_number_copy--'
//					+ current_grab_page_number_copy);
			// window.alert('test---'+para3);
			// arr[current_grab_page_number_copy].dispatchEvent(ev);
		}, current_grab_page_number);
	} else if (page.url == ("https://www.baidu.com/")) {
		console.log('search_keyword---' + search_keyword);
		page.evaluate(function(keyword) {
			if (document.getElementById("kw1")) {
				// window.alert("kw1 is exist!");
				document.querySelector('input[id="kw1"]').value = keyword;
			} else {
				// window.alert("kw1 not exist!");
				document.querySelector('input[id="kw"]').value = keyword;
			}

			if (document.getElementById("form1")) {
				document.querySelector('form[id="form1"]').submit();
			} else {
				document.querySelector('form[id="form"]').submit();
			}

			// document.querySelector('input[name="searchParaPojo.queryType"]').value='0';
			// document.querySelector('form[id="form1"]').submit();
			// var ev = document.createEvent("MouseEvents");
			// ev.initEvent("click", true, true);
			// document.querySelector('input[id="su1"]').dispatchEvent(ev);
		}, search_keyword);
		if (is_capture_pic) {
//			page.render(pic_capture_save_root_path + pic_capture_save_sub_path
//					+ pic_file_prefix_name + count_render_pic
//					+ pic_file_suffix_name);
//			count_render_pic++;
			//新的方式，以具体的翻页为准
			page.render(pic_capture_save_root_path + pic_capture_save_sub_path
					+ pic_file_prefix_name + current_grab_page_number
					+ pic_file_suffix_name);
		}
	}
	current_grab_page_number++;
};

page.onUrlChanged = function(targetUrl) {
//	console.log('New URL: ' + targetUrl);
	/*
	 * if((targetUrl.indexOf("http://www.baidu.com/s?"))>-1){
	 * page.open(targetUrl); }
	 */
};

page.onResourceTimeout = function(request) {
	console.log('self log timeout Response (#' + request.id + '): '
			+ JSON.stringify(request));
};

page.onResourceReceived = function(response) {
	// for(var i = 0; i < arguments.length; i++){
	// console.log('receive-' + JSON.stringify(arguments[i]));
	// console.log('receive-' + arguments[i]);
	// console.log('receive-' +page.cookies);
	// fs.write('kk.txt',JSON.stringify(arguments[i]),'w+');
	// }
	// console.log('resource rec page.url---'+page.url);
	// console.log('reponse url---'+response.url);
	receive_response_count_current++;
};

page.onResourceError = function(resourceError) {
	console.log('self log resource error Unable to load resource (#'
			+ resourceError.id + 'URL:' + resourceError.url + ')');
	console.log('self log resource error Error code: '
			+ resourceError.errorCode + '. Description: '
			+ resourceError.errorString);
};

function openPage(root_url) {
	page.open(root_url, function(status) {
		if (status === "success") {
			console.log('page open is success');
			// phantom.exit();
		} else {
			console.log('page open is not success,phantomjs will exit!');
			exitPhantom();
		}
		// console.log('page.open is finished');
	});
}

function closePage() {
	page.close();
}

function exitPhantom() {
	phantom.exit();
}

openPage(root_url);
