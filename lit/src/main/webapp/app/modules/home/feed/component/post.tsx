import React, { useState, useEffect, useDebugValue } from 'react';

import { Avatar, Card, CardActions, CardContent, CardHeader, CardMedia, Collapse, Button, IconButton, IconButtonProps, Popper, Typography, Container } from '@mui/material';
import MoreVertIcon from '@mui/icons-material/MoreVert';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { ReactionBarSelector, ReactionCounter, SlackSelector, PokemonSelector } from '@charkour/react-reactions';
import { GiveEmoji, ReactionsCounterList, ReactionsTypes } from './reactions';
import { ExpandMore } from './expand-more-props';
import "../feed.scss"

import PopupState, { bindToggle, bindPopper, bindHover } from 'material-ui-popup-state';
import Fade from '@mui/material/Fade';
import Paper from '@mui/material/Paper';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { UserReactionType } from 'app/shared/model/enumerations/user-reaction-type.model';
import { IPost } from "../../../../shared/model/social/post.model"

import { updateEntity, createEntity, reset, getEntitiesBySocialUser } from 'app/entities/social/user-reaction/user-reaction.reducer';
import { Co2Sharp } from '@mui/icons-material';
import { IUserReaction, defaultValue } from 'app/shared/model/social/user-reaction.model';

const FeedPost = (props: { post: IPost }) => {
    /* Current user  */
    const account = useAppSelector(state => state.authentication.account);
    const socialUser = useAppSelector(state => state.socialUser.entity);
    const userReactionEntities = useAppSelector(state => state.userReaction.entities);
    const updateSuccess = useAppSelector(state => state.userReaction.updateSuccess);
    const userReactionTypeValues = Object.keys(UserReactionType);

    const dispatch = useAppDispatch();

    const [expanded, setExpanded] = useState(false);
    const [isReactIt, setIsReacted] = useState(false);
    const [selectedReact, setReact] = useState('none');
    const [userReaction, setUserReaction] = useState(defaultValue)

    const handleExpandClick = () => {
        setExpanded(!expanded);
    };

    const reactToPost = (value: string) => {
        console.log("When I enterted the value was ", isReactIt);

        setReact(value.toUpperCase());

        console.log("userReactionEntity", userReactionEntities);
        console.log("user Entity ", selectedReact)
        console.log("Current account", account.login)

        const entityUserReaction = { post: props.post, socialUser: account.login };

        saveEntity({
            type: value.toUpperCase(),
            post: props.post.id,
            socialUser: socialUser.id
        });
    }

    useEffect(() => {
        if (updateSuccess) {
            resetAll();
        }
    }, [updateSuccess]);

    useEffect(() => {
        userReactionEntities.map(it => {
            if (it.post.id === props.post.id) {
                setIsReacted(true);
                setUserReaction(it);
                return;
            }
        })
    })
    const resetAll = () => {
        dispatch(reset());
        dispatch(getEntitiesBySocialUser(account.login));
    };

    const saveEntity = values => {

        const entity = {
            ...values,
            post: props.post,
            socialUser,
        };

        dispatch(createEntity(entity));
    };

    return (
        <Card>
            <CardHeader
                avatar={
                    <Avatar sx={{ bgcolor: 'orange' }} aria-label="recipe">
                        {props.post.socialUser.user[0]}
                    </Avatar>
                }
                action={
                    <IconButton aria-label="settings">
                        <MoreVertIcon />
                    </IconButton>
                }
                title={props.post.socialUser ? props.post.socialUser.user : 'unknown user'}
                subheader={props.post.date}
            />
            {props.post.imageContentType ? (
                <CardMedia
                    component="img"
                    height="300"
                    image={`data:${props.post.imageContentType};base64,${props.post.image}`}
                    alt="Post image"
                />) : null}

            <CardContent>
                <Typography variant="body2" color="text.secondary">
                    {props.post.content}
                </Typography>
            </CardContent>

            <CardActions disableSpacing>
                <GiveEmoji emoji={userReaction.type} />

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
                    {props.post.userReactions ? (
                        props.post.userReactions.length === 0 ? ("Be the first that react")
                            : (props.post.userReactions.length + " people react it")
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
                <span>{props.post.comments ? props.post.comments.length : "0"} comments</span>

            </CardActions>
            <Collapse in={expanded} timeout="auto" unmountOnExit>
                <CardContent>
                    {props.post.comments.map((comment, i) => (
                        <div key={`comment-${i}`}>
                            <Typography paragraph>
                                {comment.socialUser}
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