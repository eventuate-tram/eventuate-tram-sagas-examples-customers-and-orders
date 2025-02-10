package contracts.commands

org.springframework.cloud.contract.spec.Contract.make {

    label 'reserveCredit'
    input {
        triggeredBy('reserveCredit()')
    }

    outputMessage {
        sentTo('customerService')
        body([
                customerId: 101,
                orderId: 102,
                orderTotal: 103
        ])
        headers {
            header('command_type','io.eventuate.examples.tram.sagas.ordersandcustomers.customers.messaging.commands.ReserveCreditCommand')
            header('command_reply_to', 'reserveCreditReply')
        }
    }
}
