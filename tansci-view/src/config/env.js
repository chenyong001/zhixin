const host = {
    base:'',// //ykai.itjcloud.com
    asrBase:''
}
console.log('env.MODE-',import.meta.env.MODE)
if(import.meta.env.MODE === "development"){
    host.base = ''
    host.asrBase = ''
}
export default {
    host
}