package contracts.apigateway

import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    request {
        method 'POST'
        url '/customers'
        body('''{"name" : "Chris", "creditLimit" : { "amount" : "123.45" }}''')
        headers{
            header('Content-Type': 'application/json')
        }
    }

    response {
        status 200
        headers {
            header('Content-Type': 'application/json')
        }
        body('''{"customerId" : "101"}''')
    }
}