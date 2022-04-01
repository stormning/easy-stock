import vm from "@/main.js"

const Bus = {}

Bus.on = function (event, callback) {
    vm.$on(event, msg => {
        console.log("receive bus event " + event)
        callback && callback(msg)
    })
}

Bus.publish = function (event, data) {
    console.log("publish bus event " + event)
    vm.$emit(event, data)
}

export default Bus;