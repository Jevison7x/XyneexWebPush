document.addEventListener('DOMContentLoaded', function(event){

    var subscriptionButton = document.getElementById('subscriptionButton');

    navigator.serviceWorker.register('service-worker.js');

    navigator.serviceWorker.ready
            .then(function(registration){
                console.log('service worker registered');
                subscriptionButton.removeAttribute('disabled');

                return registration.pushManager.getSubscription();
            }).then(function(subscription){
        if(subscription){
            console.log('Already subscribed', subscription.endpoint);
            setUnsubscribeButton();
        }else{
            setSubscribeButton();
        }
    });
});

function subscribe(){
    navigator.serviceWorker.ready
            .then(async function(registration){
                /*
                 const response = await fetch('./vapidPublicKey');
                 const vapidPublicKey = await response.text();
                 const convertedVapidKey = urlBase64ToUint8Array(vapidPublicKey);
                 * */
                return registration.pushManager.subscribe({
                    userVisibleOnly: true,
                    applicationServerKey: 'BIt2gtcK6xq5yBOiiNQMCu4FGeua3KKua2-eSrseL74FwkzvVpThbToNrfohzQjWw_9M5ccJjNpS3lKL3xool1c'
                });
            }).then(function(subscription){
        console.log('Subscribed: ', subscription.endpoint);
        console.log('Subscription: ', JSON.stringify(subscription));
        // Get public key and user auth from the subscription object
        var key = subscription.getKey ? subscription.getKey('p256dh') : '';
        var auth = subscription.getKey ? subscription.getKey('auth') : '';
        return fetch('subscribe-push', {
            method: 'post',
            headers: {
                'Content-type': 'application/json'
            },
            body: JSON.stringify({
                endpoint: subscription.endpoint,
                // Take byte[] and turn it into a base64 encoded string suitable for
                // POSTing to a server over HTTP
                key: key ? btoa(String.fromCharCode.apply(null, new Uint8Array(key))) : '',
                auth: auth ? btoa(String.fromCharCode.apply(null, new Uint8Array(auth))) : ''
            })
        });
    }).then(setUnsubscribeButton);
}

function unsubscribe(){
    navigator.serviceWorker.ready
            .then(function(registration){
                return registration.pushManager.getSubscription();
            }).then(function(subscription){
        return subscription.unsubscribe()
                .then(function(){
                    console.log('Unsubscribed', subscription.endpoint);
                    return fetch('unregister', {
                        method: 'post',
                        headers: {
                            'Content-type': 'application/json'
                        },
                        body: JSON.stringify({
                            subscription: subscription
                        })
                    });
                });
    }).then(setSubscribeButton);
}

function setSubscribeButton(){
    subscriptionButton.onclick = subscribe;
    subscriptionButton.textContent = 'Subscribe!';
}

function setUnsubscribeButton(){
    subscriptionButton.onclick = unsubscribe;
    subscriptionButton.textContent = 'Unsubscribe!';
}