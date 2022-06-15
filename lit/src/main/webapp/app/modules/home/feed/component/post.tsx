import React, { useState, useEffect } from 'react';

import { Avatar, Card, CardActions, CardContent, CardHeader, CardMedia, Collapse, Button, IconButton, IconButtonProps, Popper, Typography, Container } from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { ReactionBarSelector, ReactionCounter, SlackSelector, PokemonSelector } from '@charkour/react-reactions';
import { ReactionsCounterList, ReactionsTypes } from './reactions';
import { ExpandMore } from './expand-more-props';
import "../feed.scss"

import PopupState, { bindToggle, bindPopper, bindHover } from 'material-ui-popup-state';
import Fade from '@mui/material/Fade';
import Paper from '@mui/material/Paper';

const FeedPost = ({ post }) => {
    const [expanded, setExpanded] = useState(false);

    const handleExpandClick = () => {
        setExpanded(!expanded);
    };

    const reactToPost = (value) => {
        console.log(value);
    }

    return (
        <Card>
            <CardHeader
                avatar={
                    <Avatar sx={{ bgcolor: 'orange' }} aria-label="recipe">
                        {post.socialUser.user[0]}
                    </Avatar>
                }
                action={
                    <IconButton aria-label="settings">
                        <MoreVertIcon />
                    </IconButton>
                }
                title={post.socialUser ? post.socialUser.user : 'unknown user'}
                subheader={post.date}
            />
            {post.imageContentType ? (
                <CardMedia
                    component="img"
                    height="300"
                    image={`data:${post.imageContentType};base64,${post.image}`}
                    alt="Post image"
                />) : null}

            <CardContent>
                <Typography variant="body2" color="text.secondary">
                    {post.content}
                </Typography>
            </CardContent>



            <CardActions disableSpacing>
                <PopupState variant="popper" popupId="demo-popup-popper">
                    {(popupState) => (
                        <div>
                            <Container {...bindHover(popupState)}>
                                <ReactionCounter reactions={ReactionsCounterList} showReactsOnly={true} />
                            </Container>

                            <Popper {...bindPopper(popupState)} transition
                                placement="top-start"
                                modifiers={[
                                    {
                                        name: 'flip',
                                        enabled: true,
                                        options: {
                                            altBoundary: true,
                                            rootBoundary: 'viewport',
                                            padding: 8,
                                        },
                                    },
                                    {
                                        name: 'preventOverflow',
                                        enabled: true,
                                        options: {
                                            altAxis: true,
                                            altBoundary: true,
                                            tether: true,
                                            rootBoundary: 'viewport',
                                            padding: 8,
                                        },
                                    },
                                ]}
                            >
                                {({ TransitionProps }) => (
                                    <Fade {...TransitionProps} timeout={350}>
                                        <div>
                                            <ReactionBarSelector reactions={ReactionsTypes} onSelect={reactToPost} />
                                        </div>
                                    </Fade>
                                )}
                            </Popper>
                        </div>
                    )}
                </PopupState>
                <span className="postLikeCounter" >
                    {post.userReactions ? (
                        post.userReactions.length === 0 ? "Be the first that react" : post.userReactions.length + " people react it"
                    ) : null}
                </span>

                <ExpandMore
                    expand={expanded}
                    onClick={handleExpandClick}
                    aria-expanded={expanded}
                    aria-label="show more"
                >
                    <ExpandMoreIcon />
                </ExpandMore>
                <span>{post.comments ? post.comments.length : "0"} comments</span>

            </CardActions>
            <Collapse in={expanded} timeout="auto" unmountOnExit>
                <CardContent>
                    {post.comments.map((comment, i) => (
                        <div key={`comment-${i}`}>
                            <Typography paragraph>
                                {comment.user}
                            </Typography>
                            <Typography paragraph>
                                {comment.content}
                            </Typography>
                        </div>
                    ))}
                </CardContent>
            </Collapse>
        </Card>
    );
}

export default FeedPost;