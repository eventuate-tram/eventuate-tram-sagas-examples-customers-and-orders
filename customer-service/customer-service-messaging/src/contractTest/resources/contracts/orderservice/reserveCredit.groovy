package contracts.orderservice

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    label 'authorizeX'
    input {
        messageFrom('customerService')
        messageBody('''{"customerId":1511300065921,"orderId":1,"orderTotal": { "amount" : "61.70"}}''')
        messageHeaders {
            header('command_type','io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.messaging.commands.ReserveCreditCommand')
            header('command_saga_type','io.eventuate.examples.tram.sagas.ordersandcustomers.orders.sagas.CreateOrderSaga')
            header('command_saga_id',$(consumer(regex('[0-9a-f]{16}-[0-9a-f]{16}'))))
            header('command_reply_to', 'io.eventuate.examples.tram.sagas.ordersandcustomers.orders.sagas.CreateOrderSaga-reply')
        }
    }

    outputMessage {
        sentTo('io.eventuate.examples.tram.sagas.ordersandcustomers.orders.sagas.CreateOrderSaga-reply')
        body('''{}''')
        headers {
            header('reply_type', 'io.eventuate.examples.tram.sagas.ordersandcustomers.customers.api.messaging.replies.CustomerCreditReserved')
            header('reply_outcome-type', 'SUCCESS')
        }
    }
}