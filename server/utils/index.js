/**
 * Created by AbhishekK on 9/13/2016.
 */

exports.arrayUnique = function arrayUnique(array) {
        var a = array.concat();
        for(var i=0; i<a.length; ++i) {
            for(var j=i+1; j<a.length; ++j) {
                if(a[i] === a[j])
                    a.splice(j--, 1);
            }
        }
        return a;
    };


exports.ab2str =  function ab2str(buf) {
        return String.fromCharCode.apply(null, new Uint16Array(buf));
    };

