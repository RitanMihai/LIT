import React, { useState } from 'react';
import { Button, Card, CardBody, CardSubtitle, CardText, CardTitle } from 'reactstrap';
import './rightbar.scss';

const RightBar =() => {
    return (
    <div className="rightbar">
      <div className="rightbarWrapper">
          <Card style={{padding:'20px'}}>
              Some analitic data to be placed here,
              Like Beste performing stocks from previous day
          </Card>
      </div> 
    </div>
    )
}

export default RightBar;