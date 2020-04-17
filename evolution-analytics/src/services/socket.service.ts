/*
    Service permitting to:
        - define a default web server client page
        - define a listener on the web server given port
        - define a socket permitting to communicate between the server and the client
 */
export class SocketService {

    runSocket = (app, httpServer, port, ioSocket): void => {
        //Welcome page
        app.get(`/`, (req, res) => {
            res.send(`<h1>Typhon Evolution Analytics server. Client on port 5000 by default</h1>`);
        });

        //Server listening on the given port (3000 by default)
        httpServer.listen(port, () => {
            console.log(`listening on *:${port}`);
        });

        //Socket defined of the server, managing the communication between the server and the client
        ioSocket.on(`connection`, (socket) => {
            console.log(`Client connected`);
            socket.on(`disconnect`, () => {
                console.log(`Client disconnected`);
            });
            socket.on(`client-message`, (msg) => {
                console.log(`Client message: ` + msg);
                ioSocket.emit(`server-message`, `Hello from Node backend, in response to the message: ${msg}`);
                ioSocket.emit(`chart-update`, [463, 513, 563, 613, 663, 713, 1200]);
            });
        });
    };

}
