import React, {useCallback, useState} from 'react';
import {Card, Dropdown} from "react-bootstrap-v5";
import ServersTable, {ServerSortOption} from "../../table/ServersTable";
import {faNetworkWired} from "@fortawesome/free-solid-svg-icons";
import {useTranslation} from "react-i18next";
import {FontAwesomeIcon as Fa} from "@fortawesome/react-fontawesome";
import DropdownToggle from "react-bootstrap-v5/lib/esm/DropdownToggle";
import DropdownMenu from "react-bootstrap-v5/lib/esm/DropdownMenu";
import DropdownItem from "react-bootstrap-v5/lib/esm/DropdownItem";

const SortDropDown = ({sortBy, sortReversed, setSortBy}) => {
    const {t} = useTranslation();

    const sortOptions = Object.values(ServerSortOption);

    const getSortIcon = useCallback(() => {
        return sortReversed ? sortBy.iconDesc : sortBy.iconAsc;
    }, [sortBy, sortReversed]);

    return (
        <Dropdown className="float-end" style={{position: "absolute", right: "0.5rem"}}>
            <DropdownToggle variant=''>
                <Fa icon={getSortIcon()}/> {t(sortBy.label)}
            </DropdownToggle>

            <DropdownMenu>
                <h6 className="dropdown-header">{t('html.label.sortBy')}</h6>
                {sortOptions.map((option, i) => (
                    <DropdownItem key={i} onClick={() => setSortBy(option)}>
                        {t(option.label)}
                    </DropdownItem>
                ))}
            </DropdownMenu>
        </Dropdown>
    )
}

const ServersTableCard = ({servers, onSelect}) => {
    const {t} = useTranslation();
    const [sortBy, setSortBy] = useState(ServerSortOption.ALPHABETICAL);
    const [sortReversed, setSortReversed] = useState(false);

    const setSort = option => {
        if (sortBy === option) {
            setSortReversed(!sortReversed);
        } else {
            setSortBy(option);
            setSortReversed(false);
        }
    }

    return (
        <Card>
            <Card.Header style={{width: "100%"}}>
                <h6 className="col-black">
                    <Fa icon={faNetworkWired} className={"col-light-green"}/> {t('html.label.servers')}
                </h6>
                <SortDropDown sortBy={sortBy} setSortBy={setSort} sortReversed={sortReversed}/>
            </Card.Header>
            {!servers.length && <Card.Body>
                <p>No servers found in the database.</p>
                <p>It appears that Plan is not installed on any game servers or not connected to the same database.
                    See <a href="https://github.com/plan-player-analytics/Plan/wiki">wiki</a> for Network tutorial.</p>
            </Card.Body>}
            {servers.length && <ServersTable servers={servers}
                                             onSelect={onSelect}
                                             sortBy={sortBy}
                                             sortReversed={sortReversed}/>}
        </Card>
    )
};

export default ServersTableCard