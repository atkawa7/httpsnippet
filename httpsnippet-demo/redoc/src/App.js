import React, { Component } from 'react';
import { RedocStandalone } from 'redoc';

class App extends Component {
    render() {
        return (
            <RedocStandalone specUrl="/v2/api-docs" />
    );
    }
}

export default App;
