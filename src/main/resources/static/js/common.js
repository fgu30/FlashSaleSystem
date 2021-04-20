//展示Loading
function g_showLoading() {
    var idx = layer.msg('处理中...', {icon: 16, shade: [0.5, '#f5f5f5'], scrolLbar: false, offset: '0px', time: 18080});
    return idx;
}
//salt
var g_password_salt = "1a2b3c4d"
//获取url参数 substr有bug
function g_getQueryString(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
    var r = window.Location.search.substr(1).match(reg);
    if (r != null) return unescape(r[2]);
    return null;
}

//js获取url参数
function getQueryVariable(variable) {
    var query = window.location.search.substring(1);
    //console.log(query);
    var vars = query.split("&");
    for (var i=0;i<vars.length;i++) {
        var pair = vars[i].split("=");
        if(pair[0] == variable){return pair[1];}
    }
    return(false);
};

//设定时间格式化函,使用new Date ().format("yyy-MH-dd HH:m:ss);
Date.prototype.format = function (format) {
    var args = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "H+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
    };
    if (/(y+)/.test(format))
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var i in args) {
        var n = args[i];
        if (new RegExp("(" + i + ")").test(format))
            format = format.replace(RegExp.$1, RegExp.$1.length = 1 ? n : ("00" + n).substr(("" + n).length));
    }
    return format;
}