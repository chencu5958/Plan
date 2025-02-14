import React from 'react';
import {Modal} from "react-bootstrap-v5";
import {FontAwesomeIcon as Fa} from "@fortawesome/react-fontawesome";
import {faHandPointRight} from "@fortawesome/free-regular-svg-icons";
import {useTranslation} from "react-i18next";
import {useMetadata} from "../../hooks/metadataHook";
import {Link} from "react-router-dom";

const ForgotPasswordModal = ({show, toggle}) => {
    const {t} = useTranslation();
    const {mainCommand} = useMetadata();

    return (
        <Modal id="forgotPasswordModal"
               aria-labelledby="forgotModalLabel"
               show={show}
               onHide={toggle}
        >
            <Modal.Header className="bg-white">
                <Modal.Title id="forgotModalLabel">
                    <Fa icon={faHandPointRight}/> {t('html.login.forgotPassword1')}
                </Modal.Title>
                <button aria-label="Close" className="btn-close" onClick={toggle}/>
            </Modal.Header>
            <Modal.Body className="bg-white">
                <p>{t('html.login.forgotPassword2')}</p>
                <p><code>/{mainCommand || 'plan'} unregister</code></p>
                <p>{t('html.login.forgotPassword3')}</p>
                <p><code>/{mainCommand || 'plan'} unregister [username]</code></p>
                <p>{t('html.login.forgotPassword4')} <Link to="/register"
                                                           className="col-plan">{t('html.login.register')}</Link></p>
            </Modal.Body>
        </Modal>
    )
};

export default ForgotPasswordModal