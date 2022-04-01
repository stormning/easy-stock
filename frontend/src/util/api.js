import vm from "@/main.js"
import qs from 'qs';

const promiseAllDone = function (features, callback) {
    const promises = []
    for (let f of features) {
        promises.push(new Promise(resolve => {
            f.then(() => resolve())
        }))
    }
    Promise.all(promises).then(() => callback())
}

export {
}
