package contracts.replies

org.springframework.cloud.contract.spec.Contract.make {

    label 'creditReserved'
    input {
        triggeredBy('creditReserved()')
    }

    outputMessage {
        sentTo('reserveCreditReply')
        body('''{}''')
        headers {
            header('reply_type', 'io.eventuate.examples.tram.sagas.customersandorders.customers.messaging.replies.CustomerCreditReserved')
            header('reply_outcome-type', 'SUCCESS')
        }
    }
}
