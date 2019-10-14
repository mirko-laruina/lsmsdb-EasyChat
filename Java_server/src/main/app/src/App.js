import React, { Component } from 'react';
import LoginForm from './LoginForm.js'
import ChatScreen from './ChatScreen.js'
import Cookies from 'universal-cookie';
const cookies = new Cookies();

class App extends Component{
  constructor(){
    super();
    this.state = {
      sid: cookies.get('sessionId'),
    }
  }
  
  render(){
    return (
          <div className="container">
            {this.state.sid !== undefined ? (
              <ChatScreen sid={this.state.sid} />
            ) : (
              <LoginForm />
            )}
          </div>
    )
  }
}

export default App;
