import Vue from 'vue'
import axios from 'axios'
import config from './config'

axios.defaults.baseURL = config.api;    // 设置axios的基础请求路径
axios.defaults.timeout = 15000;          // 设置axios的请求时间
axios.defaults.withCredentials=true     //让ajax携带cookie


Vue.prototype.$http = axios;
