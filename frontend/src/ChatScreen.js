import React, {Component} from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import './chat.css';
import ChatsList from './ChatsList.js'
import ChatMessages from './ChatMessages.js'

class ChatScreen extends Component {
  render() {
    return (
      <div className="chat">
        <div class="row">
            <div class="col-md-4"><ChatsList/></div>
            <div class="col-md-8">
                <ChatMessages />
            </div>
        </div>
      </div>
    );
  }
}

export default ChatScreen;
